package cl.dsy1105.cocinacapital.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RestauranteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(restaurante: RestauranteEntity)

    @Query("UPDATE restaurantes SET emailDueno = :nuevo WHERE emailDueno = :anterior")
    suspend fun cambiarCorreo(anterior: String, nuevo: String)

    @Query("SELECT * FROM restaurantes")
    suspend fun obtenerTodos(): List<RestauranteEntity>
}
