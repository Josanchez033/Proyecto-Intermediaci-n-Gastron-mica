package cl.dsy1105.cocinacapital.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

// Perfil del restaurante asociado a una cuenta de Local (Vendedor).
// Lo crea el Admin junto con la cuenta; después el Local lo puede editar.
@Entity(tableName = "restaurantes")
data class RestauranteEntity(
    @PrimaryKey val emailDueno: String,
    val nombre: String,
    val comuna: String,
    val direccion: String,
    val descripcion: String = "",
    val foto: String = "foto_salon"
)
