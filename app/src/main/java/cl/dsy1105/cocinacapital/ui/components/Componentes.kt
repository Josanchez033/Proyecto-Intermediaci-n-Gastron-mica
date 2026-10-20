package cl.dsy1105.cocinacapital.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.ui.theme.AzulPrimario
import cl.dsy1105.cocinacapital.ui.theme.AzulProfundo
import cl.dsy1105.cocinacapital.ui.theme.Celeste
import cl.dsy1105.cocinacapital.ui.theme.DegradadoHero

// Botón con degradado que se "encoge" al presionarlo (animación de resorte).
@Composable
fun PressButton(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val fuente = remember { MutableInteractionSource() }
    val presionado by fuente.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.94f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "escalaBoton"
    )
    Box(
        modifier = modifier
            .scale(escala)
            .clip(RoundedCornerShape(16.dp))
            .background(DegradadoHero)
            .clickable(fuente, null, onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

// Hace que su contenido entre con fundido + deslizamiento (con retraso opcional).
@Composable
fun Aparecer(retrasoMs: Int = 0, contenido: @Composable () -> Unit) {
    val estado = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = estado,
        enter = fadeIn(tween(450, retrasoMs)) +
                slideInVertically(tween(450, retrasoMs)) { it / 3 }
    ) { contenido() }
}

@Composable
fun EncabezadoDegradado(titulo: String, subtitulo: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(DegradadoHero)
            .padding(24.dp)
    ) {
        Column {
            Text(titulo, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(subtitulo, color = Color.White.copy(alpha = 0.85f))
        }
    }
}

// Tarjeta de un local (se usa en Explorar y Cerca).
@Composable
fun TarjetaLocal(appState: AppState, local: LocalEntity, indice: Int, onClick: () -> Unit) {
    Aparecer(retrasoMs = indice * 70) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(64.dp).clip(RoundedCornerShape(18.dp))
                        .background(Brush.linearGradient(listOf(Celeste, AzulPrimario))),
                    contentAlignment = Alignment.Center
                ) { Text(local.emoji, fontSize = 32.sp) }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(local.nombre, fontWeight = FontWeight.Bold, color = AzulProfundo)
                    Text(local.promocion, fontSize = 13.sp, color = Color(0xFF455A64))
                    Text(
                        "${local.comuna} · ★ ${local.rating} · %.1f km".format(appState.distanciaKm(local)),
                        fontSize = 12.sp, color = AzulPrimario
                    )
                }
                Box(
                    Modifier.clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(AzulPrimario, Celeste)))
                        .padding(8.dp, 4.dp)
                ) { Text("-${local.descuento}%", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
