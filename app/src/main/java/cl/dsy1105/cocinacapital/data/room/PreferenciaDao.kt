package cl.dsy1105.cocinacapital.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PreferenciaDao {
    // REPLACE: si el id ya existe, actualiza el puntaje de esa categoría.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(pref: PreferenciaEntity): Long

    @Query("UPDATE preferencias SET emailUsuario = :nuevo WHERE emailUsuario = :anterior")
    suspend fun cambiarCorreo(anterior: String, nuevo: String)

    @Query("DELETE FROM preferencias WHERE emailUsuario = :email")
    suspend fun borrarDeUsuario(email: String)

    @Query("SELECT * FROM preferencias WHERE emailUsuario = :email ORDER BY puntaje DESC")
    suspend fun obtenerPorUsuario(email: String): List<PreferenciaEntity>
}
