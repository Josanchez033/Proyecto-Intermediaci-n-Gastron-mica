package cl.dsy1105.cocinacapital.data

import cl.dsy1105.cocinacapital.data.room.AppDataBase
import cl.dsy1105.cocinacapital.data.room.CanjeDao
import cl.dsy1105.cocinacapital.data.room.CanjeEntity
import cl.dsy1105.cocinacapital.data.room.ConteoCanjes
import cl.dsy1105.cocinacapital.data.room.LocalDao
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.room.PreferenciaDao
import cl.dsy1105.cocinacapital.data.room.PreferenciaEntity
import cl.dsy1105.cocinacapital.data.room.RestauranteDao
import cl.dsy1105.cocinacapital.data.room.RestauranteEntity
import cl.dsy1105.cocinacapital.data.room.UsuarioDao
import cl.dsy1105.cocinacapital.data.room.UsuarioEntity
import io.mockk.every
import io.mockk.mockk

/**
 * Base de datos en memoria para las pruebas: reemplaza a Room (que necesita un dispositivo).
 * Cada DAO guarda en una lista y se comporta como la consulta SQL que imita.
 */
class BaseFalsa {
    val usuarios = mutableListOf<UsuarioEntity>()
    val locales = mutableListOf<LocalEntity>()
    val canjes = mutableListOf<CanjeEntity>()
    val preferencias = mutableListOf<PreferenciaEntity>()
    val restaurantes = mutableListOf<RestauranteEntity>()

    private val usuarioDao = object : UsuarioDao {
        override suspend fun insertar(usuario: UsuarioEntity) {
            require(usuarios.none { it.email == usuario.email }) { "email repetido" }
            usuarios += usuario
        }
        override suspend fun actualizar(usuario: UsuarioEntity) {
            val i = usuarios.indexOfFirst { it.email == usuario.email }
            if (i >= 0) usuarios[i] = usuario
        }
        override suspend fun eliminar(email: String) { usuarios.removeAll { it.email == email } }
        override suspend fun obtenerTodos() = usuarios.toList()
    }

    private val localDao = object : LocalDao {
        override suspend fun insertar(local: LocalEntity): Long {
            val id = if (local.id == 0L) (locales.maxOfOrNull { it.id } ?: 0L) + 1 else local.id
            locales.removeAll { it.id == id }
            locales += local.copy(id = id)
            return id
        }
        override suspend fun insertarTodos(locales: List<LocalEntity>) { locales.forEach { insertar(it) } }
        override suspend fun actualizar(local: LocalEntity) {
            val i = locales.indexOfFirst { it.id == local.id }
            if (i >= 0) locales[i] = local
        }
        override suspend fun eliminar(local: LocalEntity) { locales.removeAll { it.id == local.id } }
        override suspend fun cambiarCorreo(anterior: String, nuevo: String) {
            locales.replaceAll { if (it.vendedorEmail == anterior) it.copy(vendedorEmail = nuevo) else it }
        }
        override suspend fun contar() = locales.size
        override suspend fun obtenerTodos() = locales.sortedBy { it.id }
    }

    private val canjeDao = object : CanjeDao {
        override suspend fun insertar(canje: CanjeEntity): Long {
            val id = if (canje.id == 0L) (canjes.maxOfOrNull { it.id } ?: 0L) + 1 else canje.id
            canjes.removeAll { it.id == id }
            canjes += canje.copy(id = id)
            return id
        }
        override suspend fun borrarDeUsuario(email: String) { canjes.removeAll { it.emailUsuario == email } }
        override suspend fun cambiarPromocion(anterior: Long, nuevo: Long) {
            canjes.replaceAll { if (it.localId == anterior) it.copy(localId = nuevo) else it }
        }
        override suspend fun obtenerPorUsuario(email: String) = canjes.filter { it.emailUsuario == email }.sortedByDescending { it.id }
        override suspend fun cambiarCorreo(anterior: String, nuevo: String) {
            canjes.replaceAll { if (it.emailUsuario == anterior) it.copy(emailUsuario = nuevo) else it }
        }
        override suspend fun contarPorLocal(ids: List<Long>) =
            canjes.filter { it.localId in ids }.groupBy { it.localId }.map { (id, l) -> ConteoCanjes(id, l.size) }
    }

    private val preferenciaDao = object : PreferenciaDao {
        override suspend fun guardar(pref: PreferenciaEntity): Long {
            val id = if (pref.id == 0L) (preferencias.maxOfOrNull { it.id } ?: 0L) + 1 else pref.id
            preferencias.removeAll { it.id == id }
            preferencias += pref.copy(id = id)
            return id
        }
        override suspend fun borrarDeUsuario(email: String) { preferencias.removeAll { it.emailUsuario == email } }
        override suspend fun cambiarCorreo(anterior: String, nuevo: String) {
            preferencias.replaceAll { if (it.emailUsuario == anterior) it.copy(emailUsuario = nuevo) else it }
        }
        override suspend fun obtenerPorUsuario(email: String) =
            preferencias.filter { it.emailUsuario == email }.sortedByDescending { it.puntaje }
    }

    private val restauranteDao = object : RestauranteDao {
        override suspend fun guardar(restaurante: RestauranteEntity) {
            restaurantes.removeAll { it.emailDueno == restaurante.emailDueno }
            restaurantes += restaurante
        }
        override suspend fun cambiarCorreo(anterior: String, nuevo: String) {
            restaurantes.replaceAll { if (it.emailDueno == anterior) it.copy(emailDueno = nuevo) else it }
        }
        override suspend fun obtenerTodos() = restaurantes.toList()
    }

    /** AppDataBase simulada con MockK que entrega los DAOs en memoria. */
    val db: AppDataBase = mockk {
        every { usuarioDao() } returns usuarioDao
        every { localDao() } returns localDao
        every { canjeDao() } returns canjeDao
        every { preferenciaDao() } returns preferenciaDao
        every { restauranteDao() } returns restauranteDao
    }
}
