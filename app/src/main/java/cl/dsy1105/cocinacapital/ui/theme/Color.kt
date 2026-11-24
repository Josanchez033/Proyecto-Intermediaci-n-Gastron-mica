package cl.dsy1105.cocinacapital.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Paleta Cocina Capital: azul petróleo + aguamarina, con avisos amarillos
// y verde para el descuento.
val Petroleo = Color(0xFF0F4C5C)
val PetroleoProfundo = Color(0xFF08313B)
val PetroleoMedio = Color(0xFF1C6878)
val Aqua50 = Color(0xFFF2FAF9)
val Aqua100 = Color(0xFFE1F2F0)
val Aqua200 = Color(0xFFC4E5E2)
val Aqua300 = Color(0xFF9CD1CC)

val Tinta = Color(0xFF14282D)        // texto principal
val TintaSuave = Color(0xFF4B6369)   // texto secundario (contraste AA sobre blanco)
val Linea = Color(0xFFD5E4E3)

val AvisoFondo = Color(0xFFFFF4C4)
val AvisoTexto = Color(0xFF5E4700)
val AvisoIcono = Color(0xFFB98A00)
val CodigoFondo = Color(0xFFFFE27A)

val Verde = Color(0xFF1B7F47)
val VerdeClaro = Color(0xFFDDF3E5)
val Rojo = Color(0xFFB3261E)

// Fondo general de las pantallas: casi blanco que baja a aguamarina.
val FondoApp = Brush.verticalGradient(listOf(Aqua50, Aqua100))
// Velo sobre fotos para que el texto blanco se lea.
val VeloFoto = Brush.verticalGradient(
    0f to Color.Transparent, 0.45f to Color(0x2208313B), 1f to Color(0xD908313B)
)
