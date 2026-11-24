package cl.dsy1105.cocinacapital.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val Esquema = lightColorScheme(
    primary = Petroleo,
    onPrimary = Color.White,
    primaryContainer = Aqua200,
    onPrimaryContainer = PetroleoProfundo,
    secondary = PetroleoMedio,
    onSecondary = Color.White,
    // secondaryContainer pinta chips seleccionados y el indicador de la barra inferior
    secondaryContainer = Aqua200,
    onSecondaryContainer = PetroleoProfundo,
    tertiary = Verde,
    onTertiary = Color.White,
    tertiaryContainer = VerdeClaro,
    onTertiaryContainer = Color(0xFF0B3D21),
    background = Aqua50,
    onBackground = Tinta,
    surface = Color.White,
    onSurface = Tinta,
    surfaceVariant = Aqua100,
    onSurfaceVariant = TintaSuave,
    surfaceContainer = Color.White,
    surfaceContainerLow = Aqua50,
    surfaceContainerHigh = Aqua100,
    outline = Color(0xFF8FAAA9),
    outlineVariant = Linea,
    error = Rojo,
    onError = Color.White
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun CocinaCapitalTheme(content: @Composable () -> Unit) {
    // Paleta propia (sin color dinámico) para mantener la identidad de marca.
    MaterialTheme(colorScheme = Esquema, typography = Typography, shapes = Formas, content = content)
}
