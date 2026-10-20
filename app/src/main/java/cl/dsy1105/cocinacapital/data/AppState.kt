package cl.dsy1105.cocinacapital.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cl.dsy1105.cocinacapital.data.remote.RetrofitClient
import cl.dsy1105.cocinacapital.data.room.AppDataBase
import cl.dsy1105.cocinacapital.data.room.CanjeEntity
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.room.PreferenciaEntity
import cl.dsy1105.cocinacapital.data.room.UsuarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Usuario(val email: String, val password: String, val rol: String)

class AppState(private val db: AppDataBase) {
    val usuarios = mutableStateListOf<Usuario>()
    val locales = mutableStateListOf<LocalEntity>()
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // Ubicación del usuario (por defecto: centro de Valparaíso).
    var latitud by mutableStateOf(-33.0458)
        private set
    var longitud by mutableStateOf(-71.6197)
        private set

    // Datos de ESTE usuario: se cargan al hacer login.
    private val preferencias = mutableStateListOf<PreferenciaEntity>()
    private val canjes = mutableStateListOf<CanjeEntity>()

    private val scope = CoroutineScope(Dispatchers.IO)

    // Carga inicial: usuarios y catálogo de locales. Si Room está vacío se
    // siembra el catálogo ficticio; luego se intenta sincronizar con la API.
    fun cargarDatos() {
        scope.launch {
            val usuariosDb = db.usuarioDao().obtenerTodos()
            usuarios.clear()
            usuarios.addAll(usuariosDb.map { Usuario(it.email, it.password, it.rol) })

            if (db.localDao().contar() == 0) db.localDao().insertarTodos(LOCALES_EJEMPLO)
            try {
                val remotos = RetrofitClient.api.obtenerOfertas()
                if (remotos.isNotEmpty()) db.localDao().insertarTodos(remotos)
            } catch (_: Exception) {
                // sin backend disponible: se trabaja con el catálogo local
            }
            locales.clear()
            locales.addAll(db.localDao().obtenerTodos())
        }
    }

    // función para registrar usuario
    fun registrarUsuario(email: String, password: String, rol: String): Boolean {
        if (usuarios.any { it.email == email }) return false
        usuarios.add(Usuario(email, password, rol)) // refleja el cambio en la UI al instante
        scope.launch {
            db.usuarioDao().insertar(UsuarioEntity(email, password, rol)) // lo persiste en SQLite
        }
        return true
    }

    // función para iniciar sesión
    fun login(email: String, password: String): Boolean {
        val user = usuarios.find { it.email == email && it.password == password } ?: return false
        usuarioActual = user
        cargarDatosDeUsuario(user.email)
        return true
    }

    // función para cerrar sesión
    fun logout() {
        usuarioActual = null
        preferencias.clear()
        canjes.clear()
    }

    private fun cargarDatosDeUsuario(email: String) {
        scope.launch {
            val prefsDb = db.preferenciaDao().obtenerPorUsuario(email)
            val canjesDb = db.canjeDao().obtenerPorUsuario(email)
            preferencias.clear(); preferencias.addAll(prefsDb)
            canjes.clear(); canjes.addAll(canjesDb)
        }
    }

    // ---------- Catálogo ----------
    fun buscarLocal(id: Long): LocalEntity? = locales.find { it.id == id }

    fun categorias(): List<String> = listOf("Todos") + locales.map { it.categoria }.distinct()

    // Distancia en km entre el usuario y un local (fórmula de Haversine).
    fun distanciaKm(l: LocalEntity): Double {
        val dLat = Math.toRadians(l.latitud - latitud)
        val dLon = Math.toRadians(l.longitud - longitud)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(latitud)) * cos(Math.toRadians(l.latitud)) * sin(dLon / 2).pow(2)
        return 2 * 6371.0 * asin(sqrt(a))
    }

    fun actualizarUbicacion(lat: Double, lon: Double) {
        latitud = lat; longitud = lon
    }

    // Puntaje de recomendación: perfil de consumo + cercanía + descuento.
    private fun puntaje(l: LocalEntity): Double {
        val afinidad = preferencias.firstOrNull { it.categoria == l.categoria }?.puntaje ?: 0
        return afinidad * 2.0 - distanciaKm(l) * 0.3 + l.descuento / 10.0
    }

    private fun filtrar(busqueda: String, categoria: String) = locales.filter {
        (categoria == "Todos" || it.categoria == categoria) &&
                (busqueda.isBlank() ||
                        (it.nombre + it.categoria + it.promocion + it.comuna).contains(busqueda, true))
    }

    // "Para ti": promociones dirigidas según el perfil de consumo y la ubicación.
    fun obtenerRecomendados(busqueda: String, categoria: String) =
        filtrar(busqueda, categoria).sortedByDescending { puntaje(it) }

    fun obtenerCercanos() = locales.sortedBy { distanciaKm(it) }

    fun mejorOferta(): LocalEntity? = locales.maxByOrNull { puntaje(it) }

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

    // El usuario revisó un local: se registra la interacción (+1).
    fun registrarVista(local: LocalEntity) = sumarPreferencia(local.categoria, 1)

    // ---------- Beneficios ----------
    fun canjear(local: LocalEntity) {
        val email = usuarioActual?.email ?: return
        if (canjes.any { it.localId == local.id }) return
        scope.launch {
            val canje = CanjeEntity(emailUsuario = email, localId = local.id, fecha = System.currentTimeMillis())
            val id = db.canjeDao().insertar(canje)
            canjes.add(0, canje.copy(id = id))
        }
        sumarPreferencia(local.categoria, 3)
    }

    fun yaCanjeado(local: LocalEntity) = canjes.any { it.localId == local.id }

    fun obtenerCanjes(): List<Pair<CanjeEntity, LocalEntity>> =
        canjes.mapNotNull { c -> buscarLocal(c.localId)?.let { c to it } }

    // ---------- Perfil vendedor ----------
    fun publicarLocal(nombre: String, categoria: String, promocion: String, descuento: Int, horario: String) {
        val nuevo = LocalEntity(
            nombre = nombre, categoria = categoria, comuna = "Valparaíso", promocion = promocion,
            descripcion = "Promoción publicada por el oferente.", descuento = descuento,
            horario = horario, rating = 5.0, latitud = -33.04, longitud = -71.62, emoji = "🍽️"
        )
        scope.launch {
            val id = db.localDao().insertar(nuevo)
            locales.add(nuevo.copy(id = id))
        }
    }
}
