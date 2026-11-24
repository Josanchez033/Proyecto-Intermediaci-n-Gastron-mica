package cl.dsy1105.cocinacapital.ui.components

import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.Aqua200
import cl.dsy1105.cocinacapital.ui.theme.Aqua300
import cl.dsy1105.cocinacapital.ui.theme.Linea
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.PetroleoProfundo
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave

/** true si el usuario quitó las animaciones del sistema (Opciones de accesibilidad). */
@Composable
fun sinAnimaciones(): Boolean {
    val context = LocalContext.current
    return remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}

/**
 * Gráfico de barras por hora que también sirve para elegir la hora (la "marea de horas").
 * Cada barra lleva su número encima y la hora debajo, así se lee sin explicación:
 * barra más alta = más mesas o locales a esa hora. Se usa en el inicio y en "Arma tu salida".
 *
 * @param datos pares (hora, cantidad), solo con las horas que se pueden elegir.
 * @param horaActual hora marcada como "ahora", o -1 si el gráfico no es de hoy.
 * @param unidad texto para lectores de pantalla: "mesa"/"mesas", "local"/"locales".
 */
@Composable
fun BarrasHoras(
    datos: List<Pair<Int, Int>>,
    seleccionada: Int,
    horaActual: Int,
    unidad: (Int) -> String,
    modifier: Modifier = Modifier,
    onSeleccion: (Int) -> Unit
) {
    val maximo = (datos.maxOfOrNull { it.second } ?: 0).coerceAtLeast(1)
    val sinAnimacion = sinAnimaciones()
    // Las barras suben desde cero al aparecer; sin animaciones del sistema quedan quietas.
    val subida = remember { Animatable(if (sinAnimacion) 1f else 0f) }
    LaunchedEffect(Unit) { subida.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }

    val estado = rememberLazyListState((datos.indexOfFirst { it.first == seleccionada } - 2).coerceAtLeast(0))
    // Si la hora elegida cambia desde fuera (otro día, otra búsqueda), la barra vuelve a quedar a la vista.
    LaunchedEffect(seleccionada, datos.firstOrNull()?.first) {
        val i = datos.indexOfFirst { it.first == seleccionada }
        if (i >= 0) {
            val visibles = estado.layoutInfo.visibleItemsInfo.map { it.index }
            if (i !in visibles.drop(1).dropLast(1)) {
                val destino = (i - 2).coerceAtLeast(0)
                if (sinAnimacion) estado.scrollToItem(destino) else estado.animateScrollToItem(destino)
            }
        }
    }

    Box(modifier.fillMaxWidth()) {
        // Línea base: la que hace que las barras se lean como un gráfico.
        Box(
            Modifier.fillMaxWidth().padding(top = 26.dp + ALTO_BARRAS).height(1.dp).background(Linea)
        )
        LazyRow(state = estado, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            items(datos, key = { it.first }) { (h, n) ->
                val elegida = h == seleccionada
                val alto by animateDpAsState(if (n == 0) 4.dp else (14f + 70f * n / maximo).dp, label = "barra")
                val color by animateColorAsState(
                    when {
                        elegida -> Petroleo
                        n == 0 -> Aqua200
                        else -> Aqua300
                    }, label = "colorBarra"
                )
                Column(
                    Modifier.width(48.dp).clip(RoundedCornerShape(12.dp))
                        .background(if (elegida) Aqua100 else Color.Transparent)
                        .clickable(onClickLabel = "Elegir las %02d:00".format(h)) { onSeleccion(h) }
                        .semantics(mergeDescendants = true) {
                            contentDescription = "%02d:00, %d %s".format(h, n, unidad(n))
                            selected = elegida
                        }
                        .padding(top = 4.dp, bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "$n", Modifier.height(22.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (elegida) FontWeight.Bold else FontWeight.Medium,
                        color = when {
                            elegida -> Petroleo
                            n == 0 -> TintaSuave.copy(alpha = 0.6f)
                            else -> TintaSuave
                        }
                    )
                    Box(Modifier.height(ALTO_BARRAS), contentAlignment = Alignment.BottomCenter) {
                        Box(
                            Modifier.width(26.dp).height(alto * subida.value)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                                .background(color)
                        )
                    }
                    Spacer(Modifier.height(7.dp))
                    Text(
                        "%02d:00".format(h), style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (elegida) FontWeight.Bold else FontWeight.Normal,
                        color = if (elegida) PetroleoProfundo else TintaSuave
                    )
                    Text(
                        if (h == horaActual) "ahora" else "", Modifier.height(16.dp),
                        style = MaterialTheme.typography.labelSmall, color = Petroleo
                    )
                }
            }
        }
    }
}

private val ALTO_BARRAS = 86.dp
