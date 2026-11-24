package cl.dsy1105.cocinacapital.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// El DAO (Data Access Object) declara QUÉ consultas SQL existen.
// Room genera automáticamente el código que las ejecuta.
@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: UsuarioEntity)

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    @Query("DELETE FROM usuarios WHERE email = :email")
    suspend fun eliminar(email: String)

    // Se usa al iniciar la app para cargar todos los usuarios a memoria.
    @Query("SELECT * FROM usuarios")
    suspend fun obtenerTodos(): List<UsuarioEntity>
}
