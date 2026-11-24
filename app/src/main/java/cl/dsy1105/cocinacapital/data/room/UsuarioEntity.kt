package cl.dsy1105.cocinacapital.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Cada fila de la tabla "usuarios". El email es la clave primaria porque
// no puede haber dos usuarios con el mismo email. "rol" puede ser
// "Comprador" (consumidor), "Vendedor" (local gastronómico, solo lo crea el
// Admin) o "Admin" (administrador de Cocina Capital, sembrado al instalar).
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val email: String,
    val password: String,
    val rol: String,
    // Un local desactivado por el Admin no puede ingresar y sus promociones se ocultan.
    @ColumnInfo(defaultValue = "1") val activo: Boolean = true,
    // Datos editables de la cuenta ("Mi cuenta").
    @ColumnInfo(defaultValue = "''") val nombre: String = "",
    val foto: String? = null, // ruta de la foto de perfil guardada en el almacenamiento interno
    // true cuando el Admin entregó una contraseña temporal: se pide cambiarla al ingresar.
    @ColumnInfo(defaultValue = "0") val claveTemporal: Boolean = false
)
