package cl.dsy1105.cocinacapital.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalDao {
    // Devuelve el id autogenerado del local publicado por un vendedor.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(local: LocalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(locales: List<LocalEntity>)

    @Query("SELECT COUNT(*) FROM locales")
    suspend fun contar(): Int

    @Query("SELECT * FROM locales ORDER BY id")
    suspend fun obtenerTodos(): List<LocalEntity>
}
