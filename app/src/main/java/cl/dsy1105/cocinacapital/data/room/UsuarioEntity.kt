package cl.dsy1105.cocinacapital.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

// Cada fila de la tabla "usuarios". El email es la clave primaria porque
// no puede haber dos usuarios con el mismo email. "rol" puede ser
// "Comprador" (consumidor) o "Vendedor" (oferente del rubro gastronómico).
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val email: String,
    val password: String,
    val rol: String
)
