package cl.dsy1105.cocinacapital.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cl.dsy1105.cocinacapital.data.Cielo
import cl.dsy1105.cocinacapital.data.Clima
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.viewmodel.EstadoClima

private fun iconoDe(clima: Clima): ImageVector = when (clima.cielo) {
    Cielo.DESPEJADO -> if (clima.esDia) Icons.Filled.WbSunny else Icons.Filled.NightsStay
    Cielo.NUBLADO, Cielo.NIEBLA -> Icons.Filled.Cloud
    Cielo.LLOVIZNA -> Icons.Filled.Grain
    Cielo.LLUVIA -> Icons.Filled.Umbrella
    Cielo.TORMENTA -> Icons.Filled.Thunderstorm
    Cielo.NIEVE -> Icons.Filled.AcUnit
}

/**
 * El clima de hoy en el inicio (API externa Open-Meteo) con una sugerencia para la salida.
 * Si la API no responde, la tarjeta no aparece y el inicio sigue igual.
 */
@Composable
fun TarjetaClima(estado: EstadoClima, comuna: String, modifier: Modifier = Modifier) {
    AnimatedContent(estado, label = "clima") { e ->
        when (e) {
            EstadoClima.NoDisponible -> Spacer(Modifier)
            EstadoClima.Cargando -> Text(
                "Consultando el clima en $comuna…", modifier.padding(vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall, color = TintaSuave
            )
            is EstadoClima.Listo -> ContenidoClima(e.clima, comuna, modifier)
        }
    }
}

@Composable
private fun ContenidoClima(clima: Clima, comuna: String, modifier: Modifier) {
    val detalle = listOfNotNull(
        clima.maxima?.let { "Máx $it°" }, clima.minima?.let { "Mín $it°" }, clima.probLluvia?.let { "Lluvia $it%" }
    ).joinToString(" · ")
    Surface(
        modifier.fillMaxWidth().semantics(mergeDescendants = true) {
            contentDescription = "Clima en $comuna: ${clima.temperatura} grados, ${clima.descripcion}. ${clima.sugerencia}"
        },
        shape = RoundedCornerShape(18.dp), color = Color.White, shadowElevation = 1.dp
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(Aqua100), contentAlignment = Alignment.Center) {
                Icon(iconoDe(clima), null, tint = Petroleo, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("${clima.temperatura}°", style = MaterialTheme.typography.titleLarge, color = Tinta)
                    Spacer(Modifier.width(6.dp))
                    Text("${clima.descripcion} en $comuna", style = MaterialTheme.typography.bodyMedium, color = TintaSuave,
                        modifier = Modifier.padding(bottom = 2.dp))
                }
                if (detalle.isNotEmpty()) Text(detalle, style = MaterialTheme.typography.bodySmall, color = TintaSuave)
                Text(clima.sugerencia, style = MaterialTheme.typography.bodyMedium, color = Petroleo)
                Text("Datos: Open-Meteo", style = MaterialTheme.typography.labelSmall, color = TintaSuave)
            }
        }
    }
}
