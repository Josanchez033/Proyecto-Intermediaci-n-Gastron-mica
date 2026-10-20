package cl.dsy1105.cocinacapital.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

// Beneficios adquiridos/canjeados por un usuario en un local.
@Entity(tableName = "canjes")
data class CanjeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val emailUsuario: String,
    val localId: Long,
    val fecha: Long
)
