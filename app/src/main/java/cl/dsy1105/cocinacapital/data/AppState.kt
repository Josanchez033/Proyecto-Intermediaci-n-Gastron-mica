package cl.dsy1105.cocinacapital.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cl.dsy1105.cocinacapital.data.remote.ActivoBody
import cl.dsy1105.cocinacapital.data.remote.CambioClaveBody
import cl.dsy1105.cocinacapital.data.remote.CanjeBody
import cl.dsy1105.cocinacapital.data.remote.ClaveTemporalBody
import cl.dsy1105.cocinacapital.data.remote.EstadoBody
import cl.dsy1105.cocinacapital.data.remote.LoginBody
import cl.dsy1105.cocinacapital.data.remote.NuevoLocalBody
import cl.dsy1105.cocinacapital.data.remote.PerfilBody
import cl.dsy1105.cocinacapital.data.remote.PromocionesApi
import cl.dsy1105.cocinacapital.data.remote.PuntosBody
import cl.dsy1105.cocinacapital.data.remote.RegistroBody
import cl.dsy1105.cocinacapital.data.remote.Remoto
import cl.dsy1105.cocinacapital.data.remote.RetrofitClient
import cl.dsy1105.cocinacapital.data.remote.UsuarioDto
import cl.dsy1105.cocinacapital.data.remote.UsuariosApi
import cl.dsy1105.cocinacapital.data.remote.llamar
import cl.dsy1105.cocinacapital.data.room.AppDataBase
import cl.dsy1105.cocinacapital.data.room.CanjeEntity
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.room.PreferenciaEntity
import cl.dsy1105.cocinacapital.data.room.RestauranteEntity
import cl.dsy1105.cocinacapital.data.room.UsuarioEntity
import androidx.room.withTransaction
import cl.dsy1105.cocinacapital.utils.errorClaveNueva
import cl.dsy1105.cocinacapital.utils.proximaFecha
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class Usuario(
    val email: String, val password: String, val rol: String, val activo: Boolean = true,
    val nombre: String = "", val foto: String? = null, val claveTemporal: Boolean = false
) {
    fun aEntidad() = UsuarioEntity(email, password, rol, activo, nombre, foto, claveTemporal)
}

private fun UsuarioEntity.aUsuario() = Usuario(email, password, rol, activo, nombre, foto, claveTemporal)

enum class ResultadoLogin { OK, INCORRECTO, DESACTIVADO }

/** Resultado de pedir un código para recuperar la contraseña. */
sealed interface Recuperacion {
    data class CodigoEnviado(val codigo: String) : Recuperacion
    data object NoExiste : Recuperacion
    data object CuentaGestionada : Recuperacion // Local o Admin: la recupera el administrador
}

/**
 * Estado de la app. Los datos viven en los microservicios (usuarios y promociones) y se guardan
 * también en Room: si el servidor no responde, la app sigue funcionando con lo guardado y avisa.
 * Las APIs se reciben por parámetro para poder probar esta clase con MockK.
 */
class AppState(
    private val db: AppDataBase,
    private val usuariosApi: UsuariosApi = RetrofitClient.usuarios,
    private val promocionesApi: PromocionesApi = RetrofitClient.promociones,
    // Hilos inyectables: las pruebas usan un dispatcher de prueba para que todo corra en orden.
    private val io: CoroutineDispatcher = Dispatchers.IO,
    private val main: CoroutineDispatcher = Dispatchers.Main
) {
    val usuarios = mutableStateListOf<Usuario>()
    val locales = mutableStateListOf<LocalEntity>()
    val restaurantes = mutableStateListOf<RestauranteEntity>()
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // Ubicación del usuario (por defecto: centro de Valparaíso).
    var latitud by mutableStateOf(-33.0458)
        private set
    var longitud by mutableStateOf(-71.6197)
        private set
    var usandoUbicacionReal by mutableStateOf(false)
        private set

    // Aviso cuando no se pudo sincronizar con el backend (RNF-26).
    var avisoSincronizacion by mutableStateOf<String?>(null)

    /** true si la última llamada a los microservicios respondió. */
    var conectado by mutableStateOf(false)
        private set

    // Criterios de la búsqueda específica (día, hora, personas, radio, preferencia).
    var criterios by mutableStateOf(criteriosPorDefecto())

    // Datos de ESTE usuario: se cargan al hacer login.
    private val preferencias = mutableStateListOf<PreferenciaEntity>()
    private val canjes = mutableStateListOf<CanjeEntity>()
    // Para el Local: canjes recibidos por cada una de sus promociones.
    private val canjesPorPromo = mutableStateMapOf<Long, Int>()

    private val scope = CoroutineScope(io)

    // Carga inicial: usuarios y catálogo. Si Room está vacío se siembra el
    // catálogo ficticio; luego se intenta sincronizar con la API.
    fun cargarDatos() {
        scope.launch {
            // Cuenta Admin sembrada desde el inicio: solo ella crea cuentas de Local.
            if (db.usuarioDao().obtenerTodos().none { it.rol == Rol.ADMIN }) {
                db.usuarioDao().insertar(UsuarioEntity(ADMIN_EMAIL, ADMIN_PASSWORD_PRUEBA, Rol.ADMIN))
            }
            // Local de demostración con su restaurante (como si el Admin lo hubiera creado).
            if (db.usuarioDao().obtenerTodos().none { it.email == LOCAL_DEMO_EMAIL }) {
                db.usuarioDao().insertar(UsuarioEntity(LOCAL_DEMO_EMAIL, LOCAL_DEMO_PASSWORD_PRUEBA, Rol.VENDEDOR))
                db.restauranteDao().guardar(RESTAURANTE_DEMO)
            }
            val usuariosDb = db.usuarioDao().obtenerTodos()
            usuarios.clear()
            usuarios.addAll(usuariosDb.map { it.aUsuario() })
            restaurantes.clear()
            restaurantes.addAll(db.restauranteDao().obtenerTodos())

            // Siembra los locales de ejemplo que falten (también los nuevos tras una actualización).
            val existentes = db.localDao().obtenerTodos().associateBy { it.id }
            val faltantes = LOCALES_EJEMPLO.filter { it.id !in existentes }
            if (faltantes.isNotEmpty()) db.localDao().insertarTodos(faltantes)
            // Promociones de ejemplo que ahora pertenecen al Local de demostración.
            LOCALES_EJEMPLO.filter { it.vendedorEmail != null }.forEach { semilla ->
                val actual = existentes[semilla.id]
                if (actual != null && actual.vendedorEmail == null) db.localDao().actualizar(actual.copy(vendedorEmail = semilla.vendedorEmail))
            }
            sincronizarCatalogo()
            sincronizarLocales()
            locales.clear()
            locales.addAll(db.localDao().obtenerTodos())
        }
    }

    // ---------- Sincronización con los microservicios ----------

    /** Anota si el servidor respondió y, si no, deja el aviso para la interfaz. */
    private fun <T> anotar(r: Remoto<T>): Remoto<T> {
        when (r) {
            is Remoto.SinConexion -> { conectado = false; avisoSincronizacion = SIN_CONEXION }
            else -> { conectado = true; if (avisoSincronizacion == SIN_CONEXION) avisoSincronizacion = null }
        }
        return r
    }

    /** Envía un cambio al servidor en segundo plano. Si falla, el cambio queda guardado en el teléfono. */
    private fun enviar(bloque: suspend () -> Any?) {
        scope.launch { anotar(llamar { bloque() }) }
    }

    /**
     * Trae el catálogo del microservicio de promociones. Las promociones que un local creó sin
     * conexión (o antes de existir el backend) se suben primero, para no perderlas.
     */
    suspend fun sincronizarCatalogo() {
        val r = anotar(llamar { promocionesApi.promociones() })
        if (r !is Remoto.Ok) return
        val remotas = r.valor.associateBy { it.id }
        val todas = db.localDao().obtenerTodos()
        // Promociones del catálogo de ejemplo que el servidor ya no tiene: alguien las eliminó, se borran aquí.
        val idsEjemplo = LOCALES_EJEMPLO.map { it.id }.toSet()
        todas.filter { it.id in idsEjemplo && it.id !in remotas }.forEach { db.localDao().eliminar(it) }
        val propiasSinSubir = todas.filter { l ->
            l.vendedorEmail != null && l.id !in idsEjemplo &&
                    remotas[l.id].let { it == null || !it.vendedorEmail.equals(l.vendedorEmail, true) || it.promocion != l.promocion }
        }
        val subidas = propiasSinSubir.mapNotNull { l ->
            (llamar { promocionesApi.crear(l) } as? Remoto.Ok)?.valor?.also { nueva ->
                db.localDao().eliminar(l)
                db.canjeDao().cambiarPromocion(l.id, nueva.id)
            }
        }
        db.localDao().insertarTodos(r.valor + subidas)
    }

    /** Cuentas de local y restaurantes del microservicio de usuarios (las ve el Admin). */
    private suspend fun sincronizarLocales() {
        val r = llamar { usuariosApi.locales() }
        if (r !is Remoto.Ok) return
        val enServidor = r.valor.map { it.usuario.email.lowercase() }.toSet()
        r.valor.forEach { c ->
            guardarUsuarioLocal(c.usuario, password = null)
            c.restaurante?.let { db.restauranteDao().guardar(it) }
        }
        // Locales creados en este teléfono antes de conectar el backend: se suben con su restaurante.
        db.usuarioDao().obtenerTodos().filter { it.rol == Rol.VENDEDOR && it.email.lowercase() !in enServidor }.forEach { u ->
            val perfil = db.restauranteDao().obtenerTodos().firstOrNull { mismoCorreo(it.emailDueno, u.email) }
                ?: db.localDao().obtenerTodos().firstOrNull { mismoCorreo(it.vendedorEmail, u.email) }
                    ?.let { RestauranteEntity(u.email, it.nombre, it.comuna, it.direccion) }
            if (perfil != null) llamar { usuariosApi.crearLocal(NuevoLocalBody(u.email, u.password, perfil.nombre, perfil.comuna, perfil.direccion)) }
        }
        usuarios.clear(); usuarios.addAll(db.usuarioDao().obtenerTodos().map { it.aUsuario() })
        restaurantes.clear(); restaurantes.addAll(db.restauranteDao().obtenerTodos())
    }

    /** Guarda en Room la cuenta que respondió el servidor. La contraseña se conserva para entrar sin conexión. */
    private suspend fun guardarUsuarioLocal(dto: UsuarioDto, password: String?): Usuario {
        val existente = db.usuarioDao().obtenerTodos().firstOrNull { it.email.equals(dto.email, true) }
        val u = Usuario(
            existente?.email ?: dto.email, password ?: existente?.password.orEmpty(), dto.rol, dto.activo,
            dto.nombre.orEmpty(), existente?.foto ?: dto.foto, dto.claveTemporal
        )
        if (existente == null) db.usuarioDao().insertar(u.aEntidad()) else db.usuarioDao().actualizar(u.aEntidad())
        withContext(main) {
            val i = usuarios.indexOfFirst { it.email.equals(u.email, true) }
            if (i >= 0) usuarios[i] = u else usuarios.add(u)
        }
        return u
    }

    // Registro público: SOLO compradores. Las cuentas de Local las crea el Admin.
    // Se crea en el microservicio de usuarios; sin conexión, queda en el teléfono y se sube al entrar.
    suspend fun registrarComprador(email: String, password: String): Boolean = withContext(io) {
        val correo = email.trim()
        if (existeCorreo(correo)) return@withContext false
        when (val r = anotar(llamar { usuariosApi.registrar(RegistroBody(correo, password)) })) {
            is Remoto.Ok -> { guardarUsuarioLocal(r.valor, password); true }
            is Remoto.Error -> false // 409: el correo ya existe en el servidor
            Remoto.SinConexion -> {
                val u = Usuario(correo, password, Rol.COMPRADOR)
                db.usuarioDao().insertar(u.aEntidad())
                withContext(main) { usuarios.add(u) }
                true
            }
        }
    }

    fun existeCorreo(email: String) = usuarios.any { it.email.equals(email.trim(), true) }

    /**
     * Inicia sesión contra el microservicio de usuarios (contraseñas en BCrypt).
     * - Sin conexión: entra con la cuenta guardada en el teléfono.
     * - Cuenta creada en el teléfono antes de conectar el backend: se sube al servidor y entra.
     */
    suspend fun login(email: String, password: String): ResultadoLogin = withContext(io) {
        val correo = email.trim()
        val resultado = when (val r = anotar(llamar { usuariosApi.login(LoginBody(correo, password)) })) {
            is Remoto.Ok -> guardarUsuarioLocal(r.valor, password)
            is Remoto.Error -> when {
                r.codigo == 403 -> return@withContext ResultadoLogin.DESACTIVADO
                r.codigo == 401 -> subirCuentaLocal(correo, password) ?: return@withContext ResultadoLogin.INCORRECTO
                else -> return@withContext ResultadoLogin.INCORRECTO
            }
            Remoto.SinConexion -> {
                val u = usuarios.find { it.email.equals(correo, true) && it.password == password }
                    ?: return@withContext ResultadoLogin.INCORRECTO
                if (!u.activo) return@withContext ResultadoLogin.DESACTIVADO
                u
            }
        }
        withContext(main) { usuarioActual = resultado }
        cargarDatosDeUsuario(resultado.email)
        ResultadoLogin.OK
    }

    /** Si la cuenta existe solo en este teléfono (y la clave calza), la crea en el servidor. */
    private suspend fun subirCuentaLocal(correo: String, password: String): Usuario? {
        val local = usuarios.find { it.email.equals(correo, true) && it.password == password } ?: return null
        val existe = (llamar { usuariosApi.existe(correo) } as? Remoto.Ok)?.valor?.get("existe") ?: return null
        if (existe) return null // el servidor tiene otra contraseña: manda el servidor
        if (local.rol == Rol.COMPRADOR) llamar { usuariosApi.registrar(RegistroBody(local.email, password, local.nombre)) }
        return local.takeIf { it.activo }
    }

    fun logout() {
        usuarioActual = null
        preferencias.clear()
        canjes.clear()
        canjesPorPromo.clear()
        criterios = criteriosPorDefecto()
    }

    /** Vouchers y perfil de consumo del usuario: vienen del microservicio de promociones. */
    private fun cargarDatosDeUsuario(email: String) {
        scope.launch {
            val r = llamar { promocionesApi.canjes(email) }
            if (r is Remoto.Ok) {
                // Vouchers canjeados sin conexión que el servidor todavía no tiene: se suben.
                val enServidor = r.valor.map { it.localId }.toSet()
                val pendientes = db.canjeDao().obtenerPorUsuario(email).filter { it.localId !in enServidor }
                val subidos = pendientes.mapNotNull { c -> (llamar { promocionesApi.canjear(CanjeBody(email, c.localId)) } as? Remoto.Ok)?.valor }
                db.canjeDao().borrarDeUsuario(email)
                (r.valor + subidos).forEach { db.canjeDao().insertar(it) }
            }
            val p = llamar { promocionesApi.preferencias(email) }
            if (p is Remoto.Ok && p.valor.isNotEmpty()) {
                db.preferenciaDao().borrarDeUsuario(email)
                p.valor.forEach { db.preferenciaDao().guardar(it) }
            }
            val prefsDb = db.preferenciaDao().obtenerPorUsuario(email)
            val canjesDb = db.canjeDao().obtenerPorUsuario(email)
            withContext(main) {
                preferencias.clear(); preferencias.addAll(prefsDb)
                canjes.clear(); canjes.addAll(canjesDb)
            }
            cargarCanjesDelLocal()
        }
    }

    val esVendedor get() = usuarioActual?.rol == Rol.VENDEDOR
    val esAdmin get() = usuarioActual?.rol == Rol.ADMIN

    // ---------- Admin: cuentas de Local ----------
    /** Cuentas de Local con su restaurante, las más nuevas primero. */
    fun cuentasLocales(): List<Pair<Usuario, RestauranteEntity?>> =
        usuarios.filter { it.rol == Rol.VENDEDOR }.reversed()
            .map { u -> u to restaurantes.firstOrNull { mismoCorreo(it.emailDueno, u.email) } }

    fun promocionesDe(email: String) = locales.count { mismoCorreo(it.vendedorEmail, email) }

    /** Solo el Admin crea cuentas de Local, junto con el perfil de su restaurante (en el microservicio de usuarios). */
    suspend fun crearCuentaLocal(email: String, password: String, nombre: String, comuna: String, direccion: String): Boolean =
        withContext(io) {
            val correo = email.trim()
            if (!esAdmin || existeCorreo(correo)) return@withContext false
            val r = anotar(llamar { usuariosApi.crearLocal(NuevoLocalBody(correo, password, nombre.trim(), comuna.trim(), direccion.trim())) })
            if (r is Remoto.Error) return@withContext false
            val restaurante = (r as? Remoto.Ok)?.valor?.restaurante
                ?: RestauranteEntity(correo, nombre.trim(), comuna.trim(), direccion.trim())
            val u = Usuario(correo, password, Rol.VENDEDOR)
            db.usuarioDao().insertar(u.aEntidad())
            db.restauranteDao().guardar(restaurante)
            withContext(main) { usuarios.add(u); restaurantes.add(restaurante) }
            true
        }

    /** Activa o desactiva una cuenta de Local: desactivada no ingresa y sus promociones se ocultan. */
    fun cambiarActivo(email: String, activo: Boolean) {
        if (!esAdmin) return
        val i = usuarios.indexOfFirst { it.email == email && it.rol == Rol.VENDEDOR }
        if (i < 0) return
        val u = usuarios[i].copy(activo = activo)
        usuarios[i] = u
        scope.launch { db.usuarioDao().actualizar(u.aEntidad()) }
        enviar { usuariosApi.cambiarActivo(email, ActivoBody(activo)) }
    }

    /**
     * El Admin restablece la contraseña de un Local (la olvidó o hay que cambiarla).
     * Queda marcada como temporal: el Local debe reemplazarla al ingresar.
     */
    fun restablecerClaveLocal(email: String, nueva: String): Boolean {
        if (!esAdmin || errorClaveNueva(nueva) != null) return false
        val i = usuarios.indexOfFirst { it.email == email && it.rol == Rol.VENDEDOR }
        if (i < 0) return false
        guardarUsuario(i, usuarios[i].copy(password = nueva, claveTemporal = true))
        enviar { usuariosApi.claveTemporal(email, ClaveTemporalBody(nueva)) }
        return true
    }

    // ---------- Recuperar contraseña (comprador) ----------
    // Prototipo sin servidor de correo: el código se muestra en la app en vez de enviarse por email.
    private val codigosRecuperacion = mutableMapOf<String, String>()

    fun solicitarRecuperacion(email: String): Recuperacion {
        val u = usuarios.firstOrNull { it.email.equals(email.trim(), true) } ?: return Recuperacion.NoExiste
        if (u.rol != Rol.COMPRADOR) return Recuperacion.CuentaGestionada
        val codigo = (100000..999999).random().toString()
        codigosRecuperacion[u.email] = codigo
        return Recuperacion.CodigoEnviado(codigo)
    }

    fun codigoValido(email: String, codigo: String): Boolean {
        val u = usuarios.firstOrNull { it.email.equals(email.trim(), true) } ?: return false
        return codigosRecuperacion[u.email] == codigo.trim()
    }

    fun restablecerConCodigo(email: String, codigo: String, nueva: String): Boolean {
        if (!codigoValido(email, codigo) || errorClaveNueva(nueva) != null) return false
        val i = usuarios.indexOfFirst { it.email.equals(email.trim(), true) }
        codigosRecuperacion.remove(usuarios[i].email)
        guardarUsuario(i, usuarios[i].copy(password = nueva, claveTemporal = false))
        enviar { usuariosApi.restablecer(LoginBody(usuarios[i].email, nueva)) }
        return true
    }

    // ---------- Mi cuenta ----------
    private fun guardarUsuario(indice: Int, u: Usuario) {
        usuarios[indice] = u
        if (usuarioActual?.email == u.email) usuarioActual = u
        scope.launch { db.usuarioDao().actualizar(u.aEntidad()) }
    }

    private fun indiceActual() = usuarios.indexOfFirst { it.email == usuarioActual?.email }

    fun actualizarPerfil(nombre: String, foto: String?) {
        val i = indiceActual().takeIf { it >= 0 } ?: return
        guardarUsuario(i, usuarios[i].copy(nombre = nombre.trim(), foto = foto))
        val email = usuarios[i].email
        enviar { usuariosApi.actualizarPerfil(email, PerfilBody(nombre.trim(), foto.orEmpty())) }
    }

    fun claveCorrecta(clave: String) = usuarioActual?.password == clave

    /** Cambia la contraseña. Con una temporal del Admin no se pide la actual (ya la ingresó al entrar). */
    fun cambiarClave(actual: String?, nueva: String): Boolean {
        val u = usuarioActual ?: return false
        if (!u.claveTemporal && u.password != actual) return false
        if (errorClaveNueva(nueva) != null) return false
        guardarUsuario(indiceActual(), u.copy(password = nueva, claveTemporal = false))
        enviar { usuariosApi.cambiarClave(u.email, CambioClaveBody(if (u.claveTemporal) null else actual, nueva)) }
        return true
    }

    /** Cambia el correo de acceso y mueve todo lo asociado (vouchers, perfil, restaurante, promociones). */
    fun cambiarCorreo(nuevo: String, clave: String): Boolean {
        val u = usuarioActual ?: return false
        val correo = nuevo.trim()
        if (u.password != clave || existeCorreo(correo) || !cl.dsy1105.cocinacapital.utils.esEmailValido(correo)) return false
        val anterior = u.email
        val actualizado = u.copy(email = correo)
        usuarios[indiceActual()] = actualizado
        usuarioActual = actualizado
        restaurantes.indexOfFirst { it.emailDueno == anterior }.takeIf { it >= 0 }?.let { restaurantes[it] = restaurantes[it].copy(emailDueno = correo) }
        locales.forEachIndexed { i, l -> if (l.vendedorEmail == anterior) locales[i] = l.copy(vendedorEmail = correo) }
        canjes.forEachIndexed { i, c -> if (c.emailUsuario == anterior) canjes[i] = c.copy(emailUsuario = correo) }
        preferencias.forEachIndexed { i, p -> if (p.emailUsuario == anterior) preferencias[i] = p.copy(emailUsuario = correo) }
        scope.launch {
            db.withTransaction {
                db.usuarioDao().insertar(actualizado.aEntidad())
                db.canjeDao().cambiarCorreo(anterior, correo)
                db.preferenciaDao().cambiarCorreo(anterior, correo)
                db.restauranteDao().cambiarCorreo(anterior, correo)
                db.localDao().cambiarCorreo(anterior, correo)
                db.usuarioDao().eliminar(anterior)
            }
        }
        return true
    }

    private fun localActivo(l: LocalEntity) =
        l.vendedorEmail == null || usuarios.firstOrNull { mismoCorreo(it.email, l.vendedorEmail) }?.activo != false

    /** Catálogo que ve el comprador: promociones activas de locales habilitados. */
    // Las promociones vencidas salen del catálogo; las programadas se ven con su fecha de inicio.
    private fun catalogo() = locales.filter { it.activa && localActivo(it) && !terminada(it) }

    // ---------- Perfil público del restaurante (lo ve el comprador) ----------
    private fun mismoRestaurante(a: LocalEntity, b: LocalEntity) =
        if (a.vendedorEmail != null) mismoCorreo(a.vendedorEmail, b.vendedorEmail) else b.vendedorEmail == null && a.nombre == b.nombre

    /** Perfil del restaurante de una promoción. Los locales del catálogo base no tienen cuenta: se arma con sus datos. */
    fun restauranteDe(local: LocalEntity): RestauranteEntity =
        restaurantes.firstOrNull { mismoCorreo(it.emailDueno, local.vendedorEmail) }
            ?: RestauranteEntity("", local.nombre, local.comuna, local.direccion, local.descripcion, local.foto)

    /** Todas las promociones activas del mismo restaurante. */
    fun promocionesDelRestaurante(local: LocalEntity): List<LocalEntity> =
        catalogo().filter { mismoRestaurante(local, it) }
    val nombreCorto get() = usuarioActual?.nombre?.takeIf { it.isNotBlank() }?.substringBefore(" ")
        ?: usuarioActual?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: ""

    // ---------- Catálogo ----------
    fun buscarLocal(id: Long): LocalEntity? = locales.find { it.id == id }

    fun categorias(): List<String> = listOf(TODAS) + (CATEGORIAS_BASE + locales.map { it.categoria }).distinct()

    fun distanciaKm(l: LocalEntity): Double = Recomendador.distanciaKm(latitud, longitud, l.latitud, l.longitud)

    fun actualizarUbicacion(lat: Double, lon: Double) {
        latitud = lat; longitud = lon; usandoUbicacionReal = true
    }

    /** Comuna del local más cercano: sirve como "dónde estás" en la interfaz. */
    fun comunaActual(): String = catalogo().minByOrNull { distanciaKm(it) }?.comuna ?: "Valparaíso"

    fun afinidad(categoria: String) = preferencias.firstOrNull { it.categoria == categoria }?.puntaje ?: 0

    private fun puntaje(l: LocalEntity) = Recomendador.puntajeParaTi(afinidad(l.categoria), distanciaKm(l), l.descuento)

    private fun filtrar(busqueda: String, categoria: String) = catalogo().filter {
        (categoria == TODAS || it.categoria == categoria) &&
                (busqueda.isBlank() ||
                        (it.nombre + it.categoria + it.promocion + it.comuna).contains(busqueda.trim(), true))
    }

    // "Para ti": promociones ordenadas por perfil de consumo, cercanía y descuento.
    fun obtenerRecomendados(busqueda: String, categoria: String) =
        filtrar(busqueda, categoria).sortedByDescending { puntaje(it) }

    fun obtenerCercanos() = catalogo().sortedBy { distanciaKm(it) }

    fun mejorOferta(): LocalEntity? = catalogo().maxByOrNull { puntaje(it) }

    // ---------- Inicio: horas de baja demanda ----------
    /** Promociones abiertas ese día a esa hora, en el radio por defecto de la búsqueda (5 km), mejores primero. */
    fun abiertasEn(dia: Int, hora: Int, radioKm: Double = 5.0): List<LocalEntity> =
        catalogo().filter {
            Recomendador.abreEn(it, dia, hora) && Recomendador.vigenteEl(it, proximaFecha(dia)) && distanciaKm(it) <= radioKm
        }
            .sortedByDescending { puntaje(it) }

    /** Cuántas mesas con descuento hay cada hora del día (la "marea de horas" del inicio). */
    fun mesasPorHora(dia: Int, horas: IntRange = 8..22): List<Pair<Int, Int>> =
        horas.map { h -> h to abiertasEn(dia, h).size }

    // ---------- Búsqueda específica ----------
    fun obtenerPropuestas(c: CriteriosBusqueda = criterios): List<Propuesta> =
        Recomendador.buscarPropuestas(
            catalogo(), c, preferencias.associate { it.categoria to it.puntaje }, latitud, longitud
        )

    /** Si nada calza exacto: lo más parecido el mismo día (otra hora, más lejos u otra comida). */
    fun obtenerAlternativas(c: CriteriosBusqueda = criterios): List<Propuesta> =
        Recomendador.buscarAlternativas(
            catalogo(), c, preferencias.associate { it.categoria to it.puntaje }, latitud, longitud
        )

    // ---------- Perfil de consumo ----------
    fun obtenerPreferencias(): List<PreferenciaEntity> = preferencias.sortedByDescending { it.puntaje }

    private fun sumarPreferencia(categoria: String, puntos: Int) {
        val email = usuarioActual?.email ?: return
        scope.launch {
            val existente = preferencias.firstOrNull { it.categoria == categoria }
            val nueva = existente?.copy(puntaje = existente.puntaje + puntos)
                ?: PreferenciaEntity(emailUsuario = email, categoria = categoria, puntaje = puntos)
            val id = db.preferenciaDao().guardar(nueva)
            val guardada = nueva.copy(id = id)
            if (existente != null) preferencias[preferencias.indexOf(existente)] = guardada
            else preferencias.add(guardada)
        }
    }

    // El usuario revisó un local: se registra la interacción (+1), también en el servidor.
    fun registrarVista(local: LocalEntity) {
        sumarPreferencia(local.categoria, 1)
        val email = usuarioActual?.email ?: return
        enviar { promocionesApi.sumarPuntos(PuntosBody(email, local.categoria, 1)) }
    }

    // ---------- Beneficios ----------
    fun canjear(local: LocalEntity, alTerminar: (CanjeEntity) -> Unit = {}) {
        val email = usuarioActual?.email ?: return
        if (canjes.any { it.localId == local.id }) return
        scope.launch {
            // El canje se registra en el microservicio (suma +3 al perfil allá); sin conexión, queda en el teléfono.
            val remoto = anotar(llamar { promocionesApi.canjear(CanjeBody(email, local.id)) })
            val canje = (remoto as? Remoto.Ok)?.valor
                ?: CanjeEntity(emailUsuario = email, localId = local.id, fecha = System.currentTimeMillis())
            val id = db.canjeDao().insertar(canje)
            val guardado = canje.copy(id = id)
            withContext(main) {
                canjes.add(0, guardado)
                alTerminar(guardado)
            }
        }
        sumarPreferencia(local.categoria, 3)
    }

    fun yaCanjeado(local: LocalEntity) = canjes.any { it.localId == local.id }

    fun canjeDe(local: LocalEntity): CanjeEntity? = canjes.firstOrNull { it.localId == local.id }

    fun buscarCanje(id: Long): Pair<CanjeEntity, LocalEntity>? =
        canjes.firstOrNull { it.id == id }?.let { c -> buscarLocal(c.localId)?.let { c to it } }

    fun obtenerCanjes(): List<Pair<CanjeEntity, LocalEntity>> =
        canjes.mapNotNull { c -> buscarLocal(c.localId)?.let { c to it } }

    // ---------- Perfil vendedor (Local): su restaurante y sus promociones ----------
    fun miRestaurante(): RestauranteEntity? = restaurantes.firstOrNull { mismoCorreo(it.emailDueno, usuarioActual?.email) }

    /** Todas sus promociones: activas primero y luego las pausadas (historial), más nuevas arriba. */
    fun misPromociones(): List<LocalEntity> =
        locales.filter { mismoCorreo(it.vendedorEmail, usuarioActual?.email) }
            .sortedWith(compareByDescending<LocalEntity> { it.activa }.thenByDescending { it.creada }.thenByDescending { it.id })

    fun canjesDe(localId: Long) = canjesPorPromo[localId] ?: 0

    private fun cargarCanjesDelLocal() {
        if (!esVendedor) return
        val email = usuarioActual?.email ?: return
        scope.launch {
            // Los canjes de todos los compradores están en el servidor; sin conexión se cuentan los del teléfono.
            val conteo = (llamar { promocionesApi.conteo(email) } as? Remoto.Ok)?.valor
                ?: db.canjeDao().contarPorLocal(misPromociones().map { it.id })
            withContext(main) { canjesPorPromo.clear(); conteo.forEach { canjesPorPromo[it.localId] = it.total } }
        }
    }

    /** Crea (id = 0) o actualiza una promoción. Nombre, comuna y dirección vienen del perfil del restaurante. */
    fun guardarPromocion(local: LocalEntity) {
        val email = usuarioActual?.email ?: return
        val perfil = miRestaurante()
        val referencia = misPromociones().firstOrNull() // misma ubicación que sus otras promociones
        val conDueno = local.copy(
            vendedorEmail = email,
            nombre = perfil?.nombre ?: local.nombre,
            comuna = perfil?.comuna ?: local.comuna,
            direccion = perfil?.direccion ?: local.direccion,
            descripcion = perfil?.descripcion?.ifBlank { null } ?: local.descripcion,
            latitud = if (local.id == 0L) referencia?.latitud ?: latitud else local.latitud,
            longitud = if (local.id == 0L) referencia?.longitud ?: longitud else local.longitud,
            creada = if (local.id == 0L) System.currentTimeMillis() else local.creada
        )
        scope.launch {
            if (conDueno.id == 0L) {
                // El microservicio asigna el id; sin conexión lo asigna Room y se sube en la próxima sincronización.
                val remota = (anotar(llamar { promocionesApi.crear(conDueno) }) as? Remoto.Ok)?.valor
                val guardada = remota ?: conDueno.copy(id = db.localDao().insertar(conDueno))
                if (remota != null) db.localDao().insertar(remota)
                withContext(main) { locales.add(guardada) }
            } else {
                db.localDao().actualizar(conDueno)
                withContext(main) {
                    val i = locales.indexOfFirst { it.id == conDueno.id }
                    if (i >= 0) locales[i] = conDueno
                }
                anotar(llamar { promocionesApi.actualizar(conDueno.id, conDueno) })
            }
        }
    }

    /** Pausar (pasa al historial y deja de verse) o reactivar una promoción. */
    fun cambiarEstadoPromocion(local: LocalEntity, activa: Boolean) {
        if (!mismoCorreo(local.vendedorEmail, usuarioActual?.email)) return
        val nueva = local.copy(activa = activa)
        val i = locales.indexOfFirst { it.id == local.id }
        if (i >= 0) locales[i] = nueva
        scope.launch { db.localDao().actualizar(nueva) }
        enviar { promocionesApi.cambiarEstado(local.id, EstadoBody(activa)) }
    }

    fun eliminarPromocion(local: LocalEntity) {
        locales.removeAll { it.id == local.id }
        scope.launch { db.localDao().eliminar(local) }
        enviar { promocionesApi.eliminar(local.id) }
    }

    /** El Local edita su perfil; sus promociones toman el nuevo nombre, dirección y descripción. */
    fun guardarRestaurante(nombre: String, comuna: String, direccion: String, descripcion: String, foto: String) {
        val email = usuarioActual?.email ?: return
        if (!esVendedor) return
        val perfil = RestauranteEntity(email, nombre.trim(), comuna.trim(), direccion.trim(), descripcion.trim(), foto)
        val i = restaurantes.indexOfFirst { it.emailDueno == email }
        if (i >= 0) restaurantes[i] = perfil else restaurantes.add(perfil)
        val actualizadas = misPromociones().map {
            it.copy(nombre = perfil.nombre, comuna = perfil.comuna, direccion = perfil.direccion,
                descripcion = perfil.descripcion.ifBlank { it.descripcion })
        }
        actualizadas.forEach { a -> locales.indexOfFirst { it.id == a.id }.takeIf { it >= 0 }?.let { locales[it] = a } }
        scope.launch {
            db.restauranteDao().guardar(perfil)
            actualizadas.forEach { db.localDao().actualizar(it) }
            anotar(llamar { usuariosApi.actualizarRestaurante(email, perfil) })
            actualizadas.forEach { a -> llamar { promocionesApi.actualizar(a.id, a) } }
        }
    }

    /** Los correos no distinguen mayúsculas ("Local@gmail.com" = "local@gmail.com"), igual que en el servidor. */
    private fun mismoCorreo(a: String?, b: String?) = a != null && b != null && a.equals(b, ignoreCase = true)

    companion object {
        const val SIN_CONEXION = "Sin conexión con el servidor: usando los datos guardados en el teléfono."

        fun criteriosPorDefecto(): CriteriosBusqueda {
            val cal = Calendar.getInstance()
            // Calendar: domingo = 1 ... sábado = 7  ->  lunes = 1 ... domingo = 7
            val dia = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1
            val hora = (cal.get(Calendar.HOUR_OF_DAY) + 1).coerceIn(12, 19)
            return CriteriosBusqueda(dia, hora, 2, 5.0, TODAS)
        }
    }
}
