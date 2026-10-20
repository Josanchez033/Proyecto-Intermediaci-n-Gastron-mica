package cl.dsy1105.cocinacapital.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CanjeDao {
    @Insert
    suspend fun insertar(canje: CanjeEntity): Long

    @Query("SELECT * FROM canjes WHERE emailUsuario = :email ORDER BY id DESC")
    suspend fun obtenerPorUsuario(email: String): List<CanjeEntity>
}
