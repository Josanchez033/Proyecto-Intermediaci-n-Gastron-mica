package cl.dsy1105.cocinacapital.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

// Cada fila de la tabla "locales": el catálogo del mercado gastronómico
// (locales, promociones, horarios de baja demanda y ubicación).
// Esta misma clase se usa como modelo del JSON que entrega la API REST.
@Entity(tableName = "locales")
data class LocalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val categoria: String,
    val comuna: String,
    val promocion: String,
    val descripcion: String,
    val descuento: Int,
    val horario: String,
    val rating: Double,
    val latitud: Double,
    val longitud: Double,
    val emoji: String
)
