package cl.dsy1105.cocinacapital.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

// Perfil de consumo: cuánto le interesa a CADA usuario una categoría.
// Sube cuando revisa un local (+1) o canjea un beneficio (+3).
// Con estos puntajes se ordenan las promociones y se arman las alertas.
@Entity(tableName = "preferencias")
data class PreferenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val emailUsuario: String,
    val categoria: String,
    val puntaje: Int
)
