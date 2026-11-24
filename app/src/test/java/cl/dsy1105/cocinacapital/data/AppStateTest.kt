package cl.dsy1105.cocinacapital.data

import cl.dsy1105.cocinacapital.data.remote.CuentaLocalDto
import cl.dsy1105.cocinacapital.data.remote.PromocionesApi
import cl.dsy1105.cocinacapital.data.remote.UsuarioDto
import cl.dsy1105.cocinacapital.data.remote.UsuariosApi
import cl.dsy1105.cocinacapital.data.room.CanjeEntity
import cl.dsy1105.cocinacapital.data.room.ConteoCanjes
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.room.PreferenciaEntity
import cl.dsy1105.cocinacapital.data.room.RestauranteEntity
import cl.dsy1105.cocinacapital.data.room.UsuarioEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Pruebas de AppState, la capa que une la app con Room y con los dos microservicios.
 * Las APIs son mocks de MockK: si una llamada no está preparada, la prueba se comporta
 * como "sin conexión" (así se prueba también el modo offline).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppStateTest {

    private val hilo = UnconfinedTestDispatcher()
    private lateinit var base: BaseFalsa
    private lateinit var usuariosApi: UsuariosApi
    private lateinit var promocionesApi: PromocionesApi
    private lateinit var app: AppState

    private fun http(codigo: Int, mensaje: String = "error") =
        HttpException(Response.error<Any>(codigo, ResponseBody.create(MediaType.parse("application/json"), """{"error":"$mensaje"}""")))

    private fun dto(email: String, rol: String = Rol.COMPRADOR, activo: Boolean = true, temporal: Boolean = false) =
        UsuarioDto(email, rol, activo, "Nombre Prueba", null, temporal)

    @Before
    fun preparar() {
        Dispatchers.setMain(hilo)
        base = BaseFalsa()
        usuariosApi = mockk()
        promocionesApi = mockk()
        app = AppState(base.db, usuariosApi, promocionesApi, hilo, hilo)
    }

    @After
    fun limpiar() = Dispatchers.resetMain()

    /** Arranque sin servidor: siembra Admin, local demo y catálogo de ejemplo. */
    private fun arrancarSinServidor() = app.cargarDatos()

    private suspend fun entrarSinServidor(email: String, clave: String) =
        app.login(email, clave).also { assertEquals(ResultadoLogin.OK, it) }

    // ---------- Arranque y sincronización ----------

    @Test
    fun arranqueSinServidor_siembraDatosYAvisa() {
        arrancarSinServidor()
        assertEquals(LOCALES_EJEMPLO.size, app.locales.size)
        assertTrue(app.existeCorreo(ADMIN_EMAIL))
        assertTrue(app.existeCorreo("TRATTORIA@correo.cl")) // sin distinguir mayúsculas
        assertEquals(AppState.SIN_CONEXION, app.avisoSincronizacion)
        assertFalse(app.conectado)
        assertEquals(1, app.cuentasLocales().size)
        assertEquals(4, app.promocionesDe(LOCAL_DEMO_EMAIL))
    }

    @Test
    fun arranqueConServidor_subePromocionHechaSinConexionYCuentasLocales() {
        base.usuarios += UsuarioEntity("peru@correo.cl", "peru1234", Rol.VENDEDOR)
        base.restaurantes += RestauranteEntity("peru@correo.cl", "Comida Peruana", "Viña del Mar", "Av. Libertad 1")
        val propia = LOCALES_EJEMPLO.first().copy(id = 500, vendedorEmail = "peru@correo.cl", promocion = "Ceviche 2x1")
        base.locales += propia
        coEvery { promocionesApi.promociones() } returns LOCALES_EJEMPLO
        coEvery { promocionesApi.crear(any()) } answers { firstArg<LocalEntity>().copy(id = 901) }
        coEvery { usuariosApi.locales() } returns listOf(CuentaLocalDto(dto(LOCAL_DEMO_EMAIL, Rol.VENDEDOR), RESTAURANTE_DEMO))
        coEvery { usuariosApi.crearLocal(any()) } returns CuentaLocalDto(dto("peru@correo.cl", Rol.VENDEDOR), null)

        app.cargarDatos()

        coVerify { promocionesApi.crear(match { it.promocion == "Ceviche 2x1" }) }
        coVerify { usuariosApi.crearLocal(match { it.email == "peru@correo.cl" && it.nombre == "Comida Peruana" }) }
        assertNotNull(app.buscarLocal(901))
        assertNull(app.buscarLocal(500))
        assertTrue(app.conectado)
        assertNull(app.avisoSincronizacion)
    }

    @Test
    fun promocionDeEjemploBorradaEnElServidor_seBorraEnElTelefono() {
        coEvery { promocionesApi.promociones() } returns LOCALES_EJEMPLO.filter { it.id != 110L }
        app.cargarDatos()
        assertNull(app.buscarLocal(110))
        assertNotNull(app.buscarLocal(109))
    }

    // ---------- Login ----------

    @Test
    fun login_conServidor_guardaLaCuentaYTraeVouchersYPerfil() = runTest(hilo) {
        arrancarSinServidor()
        coEvery { usuariosApi.login(any()) } returns dto("demo@correo.cl")
        coEvery { promocionesApi.canjes("demo@correo.cl") } returns listOf(CanjeEntity(7, "demo@correo.cl", 109, 1_000))
        coEvery { promocionesApi.preferencias("demo@correo.cl") } returns listOf(PreferenciaEntity(3, "demo@correo.cl", "Italiana", 4))

        assertEquals(ResultadoLogin.OK, app.login(" demo@correo.cl ", "demo1234"))

        assertEquals("demo@correo.cl", app.usuarioActual?.email)
        assertEquals("Nombre", app.nombreCorto)
        assertEquals(4, app.afinidad("Italiana"))
        assertEquals(1, app.obtenerCanjes().size)
        assertTrue(app.yaCanjeado(app.buscarLocal(109)!!))
        assertEquals(7L, app.canjeDe(app.buscarLocal(109)!!)?.id)
        assertNotNull(app.buscarCanje(7))
        assertEquals("demo1234", base.usuarios.first { it.email == "demo@correo.cl" }.password)
        assertTrue(app.conectado)
    }

    @Test
    fun login_subeVouchersCanjeadosSinConexion() = runTest(hilo) {
        arrancarSinServidor()
        base.canjes += CanjeEntity(1, "demo@correo.cl", 2, 500)
        coEvery { usuariosApi.login(any()) } returns dto("demo@correo.cl")
        coEvery { promocionesApi.canjes(any()) } returns emptyList()
        coEvery { promocionesApi.canjear(any()) } returns CanjeEntity(40, "demo@correo.cl", 2, 900)

        app.login("demo@correo.cl", "demo1234")

        coVerify { promocionesApi.canjear(match { it.localId == 2L }) }
        assertEquals(listOf(40L), base.canjes.map { it.id })
    }

    @Test
    fun login_claveIncorrecta_401() = runTest(hilo) {
        arrancarSinServidor()
        coEvery { usuariosApi.login(any()) } throws http(401, "Correo o contraseña incorrectos")
        assertEquals(ResultadoLogin.INCORRECTO, app.login("nadie@correo.cl", "otra123"))
        assertNull(app.usuarioActual)
    }

    @Test
    fun login_localDesactivado_403() = runTest(hilo) {
        coEvery { usuariosApi.login(any()) } throws http(403)
        assertEquals(ResultadoLogin.DESACTIVADO, app.login(LOCAL_DEMO_EMAIL, LOCAL_DEMO_PASSWORD_PRUEBA))
    }

    @Test
    fun login_otroErrorDelServidor_esIncorrecto() = runTest(hilo) {
        coEvery { usuariosApi.login(any()) } throws http(500)
        assertEquals(ResultadoLogin.INCORRECTO, app.login(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA))
    }

    @Test
    fun login_sinConexion_usaLaCuentaGuardada() = runTest(hilo) {
        arrancarSinServidor()
        assertEquals(ResultadoLogin.INCORRECTO, app.login(ADMIN_EMAIL, "mala123"))
        assertEquals(ResultadoLogin.OK, app.login(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA))
        assertTrue(app.esAdmin)
        assertFalse(app.esVendedor)
        assertEquals(AppState.SIN_CONEXION, app.avisoSincronizacion)
    }

    @Test
    fun login_sinConexion_localDesactivado() = runTest(hilo) {
        arrancarSinServidor()
        entrarSinServidor(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA)
        app.cambiarActivo(LOCAL_DEMO_EMAIL, false)
        app.logout()
        assertEquals(ResultadoLogin.DESACTIVADO, app.login(LOCAL_DEMO_EMAIL, LOCAL_DEMO_PASSWORD_PRUEBA))
    }

    @Test
    fun login_cuentaQueSoloExisteEnElTelefono_seSubeAlServidor() = runTest(hilo) {
        base.usuarios += UsuarioEntity("viejo@correo.cl", "clave123", Rol.COMPRADOR)
        arrancarSinServidor()
        coEvery { usuariosApi.login(any()) } throws http(401)
        coEvery { usuariosApi.existe("viejo@correo.cl") } returns mapOf("existe" to false)
        coEvery { usuariosApi.registrar(any()) } returns dto("viejo@correo.cl")

        assertEquals(ResultadoLogin.OK, app.login("viejo@correo.cl", "clave123"))
        coVerify { usuariosApi.registrar(match { it.email == "viejo@correo.cl" }) }
    }

    @Test
    fun login_servidorTieneOtraClave_mandaElServidor() = runTest(hilo) {
        base.usuarios += UsuarioEntity("viejo@correo.cl", "clave123", Rol.COMPRADOR)
        arrancarSinServidor()
        coEvery { usuariosApi.login(any()) } throws http(401)
        coEvery { usuariosApi.existe(any()) } returns mapOf("existe" to true)
        assertEquals(ResultadoLogin.INCORRECTO, app.login("viejo@correo.cl", "clave123"))
    }

    @Test
    fun logout_limpiaLosDatosDelUsuario() = runTest(hilo) {
        arrancarSinServidor()
        entrarSinServidor(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA)
        app.logout()
        assertNull(app.usuarioActual)
        assertEquals("", app.nombreCorto)
        assertTrue(app.obtenerCanjes().isEmpty())
    }

    // ---------- Registro ----------

    @Test
    fun registro_conServidor() = runTest(hilo) {
        coEvery { usuariosApi.registrar(any()) } returns dto("nueva@correo.cl")
        assertTrue(app.registrarComprador("nueva@correo.cl", "clave123"))
        assertTrue(app.existeCorreo("nueva@correo.cl"))
        assertFalse(app.registrarComprador("NUEVA@correo.cl", "clave123")) // ya existe aquí
    }

    @Test
    fun registro_correoTomadoEnElServidor_409() = runTest(hilo) {
        coEvery { usuariosApi.registrar(any()) } throws http(409, "Ese correo ya tiene una cuenta")
        assertFalse(app.registrarComprador("tomada@correo.cl", "clave123"))
        assertFalse(app.existeCorreo("tomada@correo.cl"))
    }

    @Test
    fun registro_sinConexion_quedaEnElTelefono() = runTest(hilo) {
        assertTrue(app.registrarComprador("offline@correo.cl", "clave123"))
        assertEquals(Rol.COMPRADOR, base.usuarios.single().rol)
    }

    // ---------- Admin ----------

    @Test
    fun admin_creaLocal_enElServidor() = runTest(hilo) {
        arrancarSinServidor()
        assertFalse(app.crearCuentaLocal("x@correo.cl", "clave123", "X", "Viña del Mar", "Calle 1")) // sin sesión de Admin
        entrarSinServidor(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA)
        coEvery { usuariosApi.crearLocal(any()) } returns CuentaLocalDto(
            dto("sushi@correo.cl", Rol.VENDEDOR), RestauranteEntity("sushi@correo.cl", "Sushi Bar", "Viña del Mar", "Calle 2")
        )
        assertTrue(app.crearCuentaLocal("sushi@correo.cl", "clave123", "Sushi Bar", "Viña del Mar", "Calle 2"))
        assertEquals(2, app.cuentasLocales().size)
        assertEquals("Sushi Bar", app.cuentasLocales().first().second?.nombre)
    }

    @Test
    fun admin_creaLocal_errorDelServidor() = runTest(hilo) {
        arrancarSinServidor()
        entrarSinServidor(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA)
        coEvery { usuariosApi.crearLocal(any()) } throws http(409)
        assertFalse(app.crearCuentaLocal("sushi@correo.cl", "clave123", "Sushi", "Viña", "Calle"))
    }

    @Test
    fun admin_creaLocal_sinConexion() = runTest(hilo) {
        arrancarSinServidor()
        entrarSinServidor(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA)
        assertTrue(app.crearCuentaLocal("sushi@correo.cl", "clave123", "Sushi", "Viña", "Calle"))
        assertEquals("Sushi", base.restaurantes.first { it.emailDueno == "sushi@correo.cl" }.nombre)
    }

    @Test
    fun admin_desactivaLocal_yOcultaSusPromociones() = runTest(hilo) {
        arrancarSinServidor()
        entrarSinServidor(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA)
        coEvery { usuariosApi.cambiarActivo(any(), any()) } returns dto(LOCAL_DEMO_EMAIL, Rol.VENDEDOR, activo = false)
        val antes = app.obtenerRecomendados("", TODAS).size
        app.cambiarActivo(LOCAL_DEMO_EMAIL, false)
        coVerify { usuariosApi.cambiarActivo(LOCAL_DEMO_EMAIL, match { !it.activo }) }
        assertTrue(app.obtenerRecomendados("", TODAS).size < antes)
        app.cambiarActivo("noexiste@correo.cl", false) // no hace nada
    }

    @Test
    fun admin_restableceClaveTemporal() = runTest(hilo) {
        arrancarSinServidor()
        assertFalse(app.restablecerClaveLocal(LOCAL_DEMO_EMAIL, "TEMP2026")) // sin sesión de Admin
        entrarSinServidor(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA)
        coEvery { usuariosApi.claveTemporal(any(), any()) } returns dto(LOCAL_DEMO_EMAIL, Rol.VENDEDOR, temporal = true)
        assertFalse(app.restablecerClaveLocal(LOCAL_DEMO_EMAIL, "corta")) // no cumple la regla
        assertFalse(app.restablecerClaveLocal("noexiste@correo.cl", "TEMP2026"))
        assertTrue(app.restablecerClaveLocal(LOCAL_DEMO_EMAIL, "TEMP2026"))
        coVerify { usuariosApi.claveTemporal(LOCAL_DEMO_EMAIL, match { it.temporal == "TEMP2026" }) }
        assertTrue(base.usuarios.first { it.email == LOCAL_DEMO_EMAIL }.claveTemporal)
    }

    // ---------- Recuperar contraseña y Mi cuenta ----------

    @Test
    fun recuperarClave_soloCompradoresYConCodigo() = runTest(hilo) {
        base.usuarios += UsuarioEntity("ana@correo.cl", "clave123", Rol.COMPRADOR)
        arrancarSinServidor()
        coEvery { usuariosApi.restablecer(any()) } returns Response.success(Unit)
        assertEquals(Recuperacion.NoExiste, app.solicitarRecuperacion("nadie@correo.cl"))
        assertEquals(Recuperacion.CuentaGestionada, app.solicitarRecuperacion(LOCAL_DEMO_EMAIL))
        val codigo = (app.solicitarRecuperacion("ana@correo.cl") as Recuperacion.CodigoEnviado).codigo
        assertFalse(app.codigoValido("ana@correo.cl", "000000x"))
        assertFalse(app.codigoValido("nadie@correo.cl", codigo))
        assertTrue(app.codigoValido("ana@correo.cl", codigo))
        assertFalse(app.restablecerConCodigo("ana@correo.cl", codigo, "debil"))
        assertTrue(app.restablecerConCodigo("ana@correo.cl", codigo, "nueva456"))
        coVerify { usuariosApi.restablecer(match { it.password == "nueva456" }) }
        assertEquals(ResultadoLogin.OK, app.login("ana@correo.cl", "nueva456"))
    }

    @Test
    fun miCuenta_perfilYClave() = runTest(hilo) {
        base.usuarios += UsuarioEntity("ana@correo.cl", "clave123", Rol.COMPRADOR)
        arrancarSinServidor()
        entrarSinServidor("ana@correo.cl", "clave123")
        coEvery { usuariosApi.actualizarPerfil(any(), any()) } returns dto("ana@correo.cl")
        coEvery { usuariosApi.cambiarClave(any(), any()) } returns Response.success(Unit)

        app.actualizarPerfil("  Ana Pérez ", null)
        assertEquals("Ana", app.nombreCorto)
        coVerify { usuariosApi.actualizarPerfil("ana@correo.cl", match { it.nombre == "Ana Pérez" }) }

        assertTrue(app.claveCorrecta("clave123"))
        assertFalse(app.cambiarClave("mala999", "nueva456"))
        assertFalse(app.cambiarClave("clave123", "debil"))
        assertTrue(app.cambiarClave("clave123", "nueva456"))
        coVerify { usuariosApi.cambiarClave("ana@correo.cl", match { it.actual == "clave123" && it.nueva == "nueva456" }) }
        assertTrue(app.claveCorrecta("nueva456"))
    }

    @Test
    fun claveTemporal_noPideLaActual() = runTest(hilo) {
        base.usuarios += UsuarioEntity("loc@correo.cl", "TEMP2026", Rol.VENDEDOR, claveTemporal = true)
        arrancarSinServidor()
        entrarSinServidor("loc@correo.cl", "TEMP2026")
        coEvery { usuariosApi.cambiarClave(any(), any()) } returns Response.success(Unit)
        assertTrue(app.cambiarClave(null, "propia123"))
        coVerify { usuariosApi.cambiarClave("loc@correo.cl", match { it.actual == null }) }
        assertFalse(app.usuarioActual!!.claveTemporal)
    }

    // ---------- Comprador: perfil de consumo y canjes ----------

    @Test
    fun canjear_conServidor_guardaElVoucherDelServidor() = runTest(hilo) {
        arrancarSinServidor()
        entrarSinServidor(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA)
        val promo = app.buscarLocal(2)!!
        coEvery { promocionesApi.canjear(any()) } returns CanjeEntity(55, ADMIN_EMAIL, 2, 1234)
        coEvery { promocionesApi.sumarPuntos(any()) } returns PreferenciaEntity(1, ADMIN_EMAIL, promo.categoria, 1)
        var listo: CanjeEntity? = null

        app.registrarVista(promo)
        app.canjear(promo) { listo = it }
        app.canjear(promo) // segunda vez: no hace nada

        assertEquals(55L, listo?.id)
        assertEquals(1, app.obtenerCanjes().size)
        assertEquals(4, app.afinidad(promo.categoria)) // +1 al revisar y +3 al canjear
        assertEquals(promo.categoria, app.obtenerPreferencias().first().categoria)
        coVerify(exactly = 1) { promocionesApi.canjear(any()) }
        coVerify { promocionesApi.sumarPuntos(match { it.puntos == 1 }) }
    }

    @Test
    fun canjear_sinConexion_quedaEnElTelefono() = runTest(hilo) {
        arrancarSinServidor()
        entrarSinServidor(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA)
        app.canjear(app.buscarLocal(1)!!)
        assertEquals(1, base.canjes.size)
        assertEquals(AppState.SIN_CONEXION, app.avisoSincronizacion)
    }

    @Test
    fun sinSesion_noSeCanjeaNiSumaPerfil() {
        arrancarSinServidor()
        app.canjear(app.buscarLocal(1)!!)
        app.registrarVista(app.buscarLocal(1)!!)
        assertTrue(base.canjes.isEmpty())
        assertTrue(base.preferencias.isEmpty())
    }

    // ---------- Local: sus promociones y su restaurante ----------

    @Test
    fun local_veSusPromocionesYLosCanjesDelServidor() = runTest(hilo) {
        arrancarSinServidor()
        coEvery { usuariosApi.login(any()) } returns dto(LOCAL_DEMO_EMAIL, Rol.VENDEDOR)
        coEvery { promocionesApi.canjes(any()) } returns emptyList()
        coEvery { promocionesApi.preferencias(any()) } returns emptyList()
        coEvery { promocionesApi.conteo(LOCAL_DEMO_EMAIL) } returns listOf(ConteoCanjes(109, 3))

        app.login(LOCAL_DEMO_EMAIL, LOCAL_DEMO_PASSWORD_PRUEBA)

        assertTrue(app.esVendedor)
        assertEquals(4, app.misPromociones().size)
        assertFalse(app.misPromociones().last().activa) // las pausadas van al final
        assertEquals(3, app.canjesDe(109))
        assertEquals(0, app.canjesDe(2))
        assertEquals("La Trattoria Ascensor", app.miRestaurante()?.nombre)
    }

    @Test
    fun local_sinConexion_cuentaLosCanjesDelTelefono() = runTest(hilo) {
        base.canjes += CanjeEntity(1, "otro@correo.cl", 2, 100)
        arrancarSinServidor()
        entrarSinServidor(LOCAL_DEMO_EMAIL, LOCAL_DEMO_PASSWORD_PRUEBA)
        assertEquals(1, app.canjesDe(2))
    }

    @Test
    fun local_publicaPromocion_elServidorAsignaElId() = runTest(hilo) {
        arrancarSinServidor()
        entrarSinServidor(LOCAL_DEMO_EMAIL, LOCAL_DEMO_PASSWORD_PRUEBA)
        coEvery { promocionesApi.crear(any()) } answers { firstArg<LocalEntity>().copy(id = 950) }
        val nueva = LOCALES_EJEMPLO.first().copy(id = 0, nombre = "", promocion = "Menú ejecutivo", vendedorEmail = null)

        app.guardarPromocion(nueva)

        val guardada = app.buscarLocal(950)!!
        assertEquals(LOCAL_DEMO_EMAIL, guardada.vendedorEmail)
        assertEquals("La Trattoria Ascensor", guardada.nombre) // el nombre sale del perfil del restaurante
        assertTrue(guardada.creada > 0)
    }

    @Test
    fun local_publicaPromocion_sinConexion() = runTest(hilo) {
        arrancarSinServidor()
        entrarSinServidor(LOCAL_DEMO_EMAIL, LOCAL_DEMO_PASSWORD_PRUEBA)
        app.guardarPromocion(LOCALES_EJEMPLO.first().copy(id = 0, promocion = "Sin red"))
        assertNotNull(app.locales.firstOrNull { it.promocion == "Sin red" && it.id > 900 })
    }

    @Test
    fun local_editaPausaYEliminaPromocion() = runTest(hilo) {
        arrancarSinServidor()
        entrarSinServidor(LOCAL_DEMO_EMAIL, LOCAL_DEMO_PASSWORD_PRUEBA)
        coEvery { promocionesApi.actualizar(any(), any()) } answers { secondArg() }
        coEvery { promocionesApi.cambiarEstado(any(), any()) } answers { app.buscarLocal(firstArg())!! }
        coEvery { promocionesApi.eliminar(any()) } returns Response.success(Unit)
        val promo = app.buscarLocal(109)!!

        app.guardarPromocion(promo.copy(promocion = "Tiramisú doble"))
        assertEquals("Tiramisú doble", app.buscarLocal(109)?.promocion)
        coVerify { promocionesApi.actualizar(109, match { it.promocion == "Tiramisú doble" }) }

        app.cambiarEstadoPromocion(app.buscarLocal(109)!!, false)
        assertFalse(app.buscarLocal(109)!!.activa)
        coVerify { promocionesApi.cambiarEstado(109, match { !it.activa }) }

        app.cambiarEstadoPromocion(app.buscarLocal(1)!!, false) // no es suya: no cambia
        assertTrue(app.buscarLocal(1)!!.activa)

        app.eliminarPromocion(app.buscarLocal(109)!!)
        assertNull(app.buscarLocal(109))
        coVerify { promocionesApi.eliminar(109) }
    }

    @Test
    fun local_editaSuRestaurante_yLasPromocionesTomanLosDatos() = runTest(hilo) {
        arrancarSinServidor()
        app.guardarRestaurante("X", "Y", "Z", "", "foto") // sin sesión: no hace nada
        entrarSinServidor(LOCAL_DEMO_EMAIL, LOCAL_DEMO_PASSWORD_PRUEBA)
        coEvery { usuariosApi.actualizarRestaurante(any(), any()) } answers { secondArg() }
        coEvery { promocionesApi.actualizar(any(), any()) } answers { secondArg() }

        app.guardarRestaurante(" Trattoria Nueva ", "Viña del Mar", "Av. Perú 100", "Pastas", "foto_pasta")

        assertEquals("Trattoria Nueva", app.miRestaurante()?.nombre)
        assertTrue(app.misPromociones().all { it.nombre == "Trattoria Nueva" && it.comuna == "Viña del Mar" })
        coVerify { usuariosApi.actualizarRestaurante(LOCAL_DEMO_EMAIL, any()) }
        coVerify(exactly = 4) { promocionesApi.actualizar(any(), any()) }
    }

    // ---------- Catálogo, búsqueda y perfil público ----------

    @Test
    fun catalogo_recomendadosFiltrosYCercania() {
        arrancarSinServidor()
        val todos = app.obtenerRecomendados("", TODAS)
        assertTrue(todos.isNotEmpty())
        assertTrue(todos.none { !it.activa }) // las pausadas no se muestran
        assertTrue(app.obtenerRecomendados("sushi", TODAS).all { "sushi" in (it.nombre + it.categoria + it.promocion).lowercase() })
        assertTrue(app.obtenerRecomendados("", "Italiana").all { it.categoria == "Italiana" })
        assertTrue(app.categorias().first() == TODAS && "Italiana" in app.categorias())
        val cercanos = app.obtenerCercanos()
        assertTrue(app.distanciaKm(cercanos.first()) <= app.distanciaKm(cercanos.last()))
        assertNotNull(app.mejorOferta())
        assertTrue(app.comunaActual().isNotBlank())
    }

    @Test
    fun ubicacionReal_cambiaLaCercania() {
        arrancarSinServidor()
        app.actualizarUbicacion(-33.0245, -71.5518) // Viña del Mar
        assertTrue(app.usandoUbicacionReal)
        assertEquals(-33.0245, app.latitud, 0.0001)
        assertEquals("Viña del Mar", app.comunaActual())
    }

    @Test
    fun mareaDeHoras_yMesasAbiertas() {
        arrancarSinServidor()
        val marea = app.mesasPorHora(4)
        assertEquals(15, marea.size)
        assertTrue(marea.any { it.second > 0 })
        val abiertas = app.abiertasEn(4, 16)
        assertTrue(abiertas.all { Recomendador.abreEn(it, 4, 16) })
    }

    @Test
    fun busquedaEspecifica_propuestasYAlternativas() {
        arrancarSinServidor()
        val jueves = CriteriosBusqueda(4, 16, 4, 5.0, "Italiana")
        val propuestas = app.obtenerPropuestas(jueves)
        assertTrue(propuestas.isNotEmpty())
        assertEquals("Italiana", propuestas.first().local.categoria)
        val alternativas = app.obtenerAlternativas(jueves.copy(hora = 22, preferencia = "Carnes"))
        assertTrue(alternativas.all { it.ajuste != null })
        app.criterios = jueves
        assertEquals(propuestas.size, app.obtenerPropuestas().size)
        assertEquals(app.obtenerAlternativas(jueves).size, app.obtenerAlternativas(app.criterios).size)
    }

    @Test
    fun perfilPublico_delRestaurante() {
        arrancarSinServidor()
        val deLaTrattoria = app.buscarLocal(2)!!
        assertEquals("La Trattoria Ascensor", app.restauranteDe(deLaTrattoria).nombre)
        assertTrue(app.promocionesDelRestaurante(deLaTrattoria).all { it.vendedorEmail == LOCAL_DEMO_EMAIL && it.activa })
        val delCatalogo = app.buscarLocal(1)!!
        assertEquals(delCatalogo.nombre, app.restauranteDe(delCatalogo).nombre) // sin cuenta: se arma con sus datos
        assertTrue(app.promocionesDelRestaurante(delCatalogo).isNotEmpty())
    }
}
