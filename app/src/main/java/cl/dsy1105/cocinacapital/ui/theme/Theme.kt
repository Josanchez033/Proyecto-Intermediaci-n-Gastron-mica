package cl.dsy1105.cocinacapital.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    secondary = Celeste,
    tertiary = AzulProfundo,
    primaryContainer = Hielo,
    background = Hielo,
    surface = Color.White
)

@Composable
fun CocinaCapitalTheme(content: @Composable () -> Unit) {
    // Se usa una paleta propia (sin color dinámico) para mantener
    // la identidad blanco/celeste/azul de Cocina Capital.
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
