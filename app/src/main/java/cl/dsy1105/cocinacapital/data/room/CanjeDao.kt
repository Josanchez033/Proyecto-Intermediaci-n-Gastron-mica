package cl.dsy1105.cocinacapital.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CanjeDao {
    // REPLACE: los canjes que llegan del servidor traen su propio id.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(canje: CanjeEntity): Long

    @Query("DELETE FROM canjes WHERE emailUsuario = :email")
    suspend fun borrarDeUsuario(email: String)

    /** Cuando una promoción creada sin conexión se sube al servidor y recibe otro id. */
    @Query("UPDATE canjes SET localId = :nuevo WHERE localId = :anterior")
    suspend fun cambiarPromocion(anterior: Long, nuevo: Long)

    @Query("SELECT * FROM canjes WHERE emailUsuario = :email ORDER BY id DESC")
    suspend fun obtenerPorUsuario(email: String): List<CanjeEntity>

    @Query("UPDATE canjes SET emailUsuario = :nuevo WHERE emailUsuario = :anterior")
    suspend fun cambiarCorreo(anterior: String, nuevo: String)

    // Para el Local: cuántos canjes tuvo cada una de sus promociones.
    @Query("SELECT localId, COUNT(*) AS total FROM canjes WHERE localId IN (:ids) GROUP BY localId")
    suspend fun contarPorLocal(ids: List<Long>): List<ConteoCanjes>
}

data class ConteoCanjes(val localId: Long, val total: Int)
