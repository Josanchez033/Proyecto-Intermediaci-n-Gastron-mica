package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.CATEGORIAS_BASE
import cl.dsy1105.cocinacapital.data.CriteriosBusqueda
import cl.dsy1105.cocinacapital.data.Recomendador
import cl.dsy1105.cocinacapital.data.TODAS
import cl.dsy1105.cocinacapital.data.diaCorto
import cl.dsy1105.cocinacapital.data.nombreDia
import cl.dsy1105.cocinacapital.ui.components.AvisoBanner
import cl.dsy1105.cocinacapital.ui.components.BarraSuperior
import cl.dsy1105.cocinacapital.ui.components.BarrasHoras
import cl.dsy1105.cocinacapital.ui.components.CalendarioDialogo
import cl.dsy1105.cocinacapital.ui.components.Contador
import cl.dsy1105.cocinacapital.ui.components.EstadoVacio
import cl.dsy1105.cocinacapital.ui.components.Panel
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.components.TarjetaPromo
import cl.dsy1105.cocinacapital.ui.components.TituloSeccion
import cl.dsy1105.cocinacapital.ui.components.sinAnimaciones
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.Aqua200
import cl.dsy1105.cocinacapital.ui.theme.FondoApp
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.utils.DIAS_CALENDARIO
import cl.dsy1105.cocinacapital.utils.ULTIMA_HORA
import cl.dsy1105.cocinacapital.utils.diaSemana
import cl.dsy1105.cocinacapital.utils.diasDesdeHoy
import cl.dsy1105.cocinacapital.utils.horasDisponibles
import cl.dsy1105.cocinacapital.utils.inicioDelDia
import cl.dsy1105.cocinacapital.utils.mesCorto
import cl.dsy1105.cocinacapital.utils.numeroDia
import cl.dsy1105.cocinacapital.utils.textoFechaCorta
import cl.dsy1105.cocinacapital.utils.textoFechaLarga
import java.util.Calendar
import kotlin.math.roundToInt

private val RADIOS = listOf(1.0, 3.0, 5.0, 10.0, 20.0)
/** Días que muestra la tira de fechas; más allá se elige con el calendario. */
private const val DIAS_TIRA = 14

@Composable
private fun ChipOpcion(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionado, onClick = onClick, label = { Text(texto) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Petroleo, selectedLabelColor = Color.White, containerColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(true, seleccionado, borderColor = Aqua200)
    )
}

/**
 * Pone la búsqueda en [fecha]. Si la hora elegida ya pasó (o no existe ese día), salta a la
 * primera hora que tenga locales; así el usuario nunca queda en una hora imposible.
 */
private fun conFecha(appState: AppState, c: CriteriosBusqueda, fecha: Long, horaAhora: Int): CriteriosBusqueda {
    val nueva = c.copy(fecha = fecha, dia = diaSemana(fecha))
    val rango = horasDisponibles(fecha, horaAhora)
    if (nueva.hora in rango) return nueva
    val hora = rango.firstOrNull { appState.obtenerPropuestas(nueva.copy(hora = it)).isNotEmpty() } ?: rango.first
    return nueva.copy(hora = hora)
}

// Búsqueda específica: un "¿Cuándo?" (fecha real + barras por hora) y un "¿Con quién y qué?".
// Cada cambio se refleja al instante en las barras y en el botón, sin pasos extra.
@Composable
fun BusquedaScreen(navController: NavController, appState: AppState) {
    val horaAhora = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    // Pasadas las 22:00 ya no quedan horas hoy: la búsqueda parte mañana.
    val hoyCerrado = horaAhora > ULTIMA_HORA
    val primerDia = if (hoyCerrado) 1 else 0
    var c by remember {
        val inicial = appState.criterios
        val fecha = inicial.fecha?.takeIf { diasDesdeHoy(it) >= primerDia }
            ?: (primerDia..primerDia + 6).map { inicioDelDia(it) }.first { diaSemana(it) == inicial.dia }
        mutableStateOf(conFecha(appState, inicial, fecha, horaAhora))
    }
    var verCalendario by remember { mutableStateOf(false) }
    val fecha = c.fecha ?: inicioDelDia(primerDia)
    val esHoy = diasDesdeHoy(fecha) == 0

    val rango = horasDisponibles(fecha, horaAhora)
    val porHora = rango.map { h -> h to appState.obtenerPropuestas(c.copy(hora = h)).size }
    val disponibles = porHora.firstOrNull { it.first == c.hora }?.second ?: 0
    val alternativas = if (disponibles == 0) appState.obtenerAlternativas(c).size else 0
    val personas = if (c.personas == 1) "1 persona" else "${c.personas} personas"

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            BarraSuperior("Arma tu salida", "Te mostramos los locales que calzan contigo", onIcono = { navController.popBackStack() })
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Panel {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TituloSeccion("¿Qué día?", textoFechaLarga(fecha), Modifier.weight(1f))
                        FilledTonalIconButton(
                            onClick = { verCalendario = true }, modifier = Modifier.size(48.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Aqua100, contentColor = Petroleo)
                        ) { Icon(Icons.Filled.CalendarMonth, "Abrir calendario") }
                    }
                    TiraDias(fecha, primerDia) { c = conFecha(appState, c, it, horaAhora) }

                    Spacer(Modifier.height(4.dp))
                    TituloSeccion(
                        "¿A qué hora?",
                        if (esHoy) "Desde ahora hasta las 22:00. Cada barra muestra cuántos locales calzan a esa hora."
                        else "Cada barra muestra cuántos locales calzan a esa hora."
                    )
                    BarrasHoras(
                        porHora, c.hora, if (esHoy) horaAhora else -1,
                        unidad = { if (it == 1) "local calza" else "locales calzan" }
                    ) { c = c.copy(hora = it) }
                }
                Panel {
                    TituloSeccion("¿Cuántos son?")
                    Contador(c.personas, { c = c.copy(personas = it) }, 1, 12, if (c.personas == 1) "persona" else "personas")

                    Spacer(Modifier.height(4.dp))
                    val indiceRadio = RADIOS.indexOf(c.radioKm).coerceAtLeast(0)
                    TituloSeccion("¿Qué tan lejos?", "Desde ${appState.comunaActual()}")
                    Column {
                        Slider(
                            value = indiceRadio.toFloat(),
                            onValueChange = { c = c.copy(radioKm = RADIOS[it.roundToInt().coerceIn(0, RADIOS.lastIndex)]) },
                            valueRange = 0f..RADIOS.lastIndex.toFloat(), steps = RADIOS.size - 2,
                            modifier = Modifier.semantics {
                                contentDescription = "Distancia máxima"
                                stateDescription = "Hasta ${c.radioKm.toInt()} kilómetros"
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = Petroleo, activeTrackColor = Petroleo, inactiveTrackColor = Aqua200,
                                activeTickColor = Aqua200, inactiveTickColor = Petroleo
                            )
                        )
                        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            RADIOS.forEach { r ->
                                Text(
                                    "${r.toInt()} km", style = MaterialTheme.typography.labelMedium,
                                    color = if (r == c.radioKm) Petroleo else TintaSuave,
                                    fontWeight = if (r == c.radioKm) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                Panel {
                    TituloSeccion("¿Qué se te antoja?", "Sumamos lo que ya sabemos de tu perfil de consumo")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf(TODAS) + CATEGORIAS_BASE) { cat ->
                            ChipOpcion(cat, c.preferencia == cat) { c = c.copy(preferencia = cat) }
                        }
                    }
                }
            }
            Surface(color = Color.White, shadowElevation = 12.dp) {
                Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                    // Resumen vivo de lo elegido: confirma la búsqueda sin volver a mirar arriba.
                    Text(
                        "${textoFechaCorta(fecha)} · ${"%02d:00".format(c.hora)} · $personas · hasta ${c.radioKm.toInt()} km" +
                                if (c.preferencia != TODAS) " · ${c.preferencia}" else "",
                        style = MaterialTheme.typography.titleSmall, color = Tinta,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    val pista = when {
                        disponibles > 0 -> null
                        alternativas > 0 -> "Nada calza exacto a esa hora. Te mostramos lo más parecido ese mismo día."
                        else -> "No hay promociones para $personas ese día. Prueba otro día o menos personas."
                    }
                    if (pista != null) {
                        Text(pista, style = MaterialTheme.typography.bodySmall, color = TintaSuave)
                    }
                    Spacer(Modifier.height(10.dp))
                    AnimatedContent(disponibles to alternativas, label = "conteo") { (n, a) ->
                        PressButton(
                            when {
                                n == 1 -> "Ver 1 propuesta"
                                n > 1 -> "Ver $n propuestas"
                                a == 1 -> "Ver 1 alternativa"
                                a > 1 -> "Ver $a alternativas"
                                else -> "Sin locales para esa búsqueda"
                            },
                            Modifier.fillMaxWidth(), icono = Icons.Filled.TravelExplore, habilitado = n > 0 || a > 0
                        ) {
                            appState.criterios = c
                            navController.navigate("propuestas") { launchSingleTop = true }
                        }
                    }
                }
            }
        }
    }

    if (verCalendario) {
        CalendarioDialogo("¿Qué día sales?", fecha, primerDia, DIAS_CALENDARIO, onCerrar = { verCalendario = false }) {
            c = conFecha(appState, c, it, horaAhora)
        }
    }
}

/**
 * Las fechas reales de las próximas dos semanas, desde hoy. Lo pasado no aparece; si hoy ya
 * no quedan horas, "Hoy" se ve apagado. Una fecha más lejana (elegida en el calendario) se suma al final.
 */
@Composable
private fun TiraDias(fecha: Long, primerDia: Int, onElegir: (Long) -> Unit) {
    val elegido = diasDesdeHoy(fecha)
    val dias = (0 until DIAS_TIRA).toList().let { if (elegido >= DIAS_TIRA) it + elegido else it }
    val sinAnimacion = sinAnimaciones()
    val estado = rememberLazyListState((dias.indexOf(elegido) - 1).coerceAtLeast(0))
    LaunchedEffect(elegido) {
        val i = dias.indexOf(elegido)
        val visibles = estado.layoutInfo.visibleItemsInfo.map { it.index }
        if (i >= 0 && i !in visibles.drop(1).dropLast(1)) {
            val destino = (i - 1).coerceAtLeast(0)
            if (sinAnimacion) estado.scrollToItem(destino) else estado.animateScrollToItem(destino)
        }
    }
    LazyRow(state = estado, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(dias, key = { it }) { offset ->
            val dia = inicioDelDia(offset)
            CeldaDia(dia, offset == elegido, offset >= primerDia) { onElegir(dia) }
        }
    }
}

@Composable
private fun CeldaDia(fecha: Long, elegida: Boolean, habilitada: Boolean, onClick: () -> Unit) {
    val offset = diasDesdeHoy(fecha)
    val fondo by animateColorAsState(if (elegida) Petroleo else Color.White, label = "fondoDia")
    val texto = if (elegida) Color.White else Tinta
    val etiqueta = when (offset) {
        0 -> "Hoy"
        1 -> "Mañana"
        else -> diaCorto(diaSemana(fecha))
    }
    Column(
        Modifier.width(60.dp).clip(RoundedCornerShape(16.dp)).background(fondo)
            .border(1.dp, if (elegida) Petroleo else Aqua200, RoundedCornerShape(16.dp))
            .alpha(if (habilitada) 1f else 0.4f)
            .clickable(enabled = habilitada, onClickLabel = "Elegir este día", onClick = onClick)
            .semantics(mergeDescendants = true) {
                contentDescription = textoFechaLarga(fecha) + if (habilitada) "" else ", ya no quedan horas"
                selected = elegida
            }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = if (elegida) Color.White else TintaSuave, maxLines = 1)
        Text("${numeroDia(fecha)}", style = MaterialTheme.typography.titleLarge, color = texto)
        Text(mesCorto(fecha), style = MaterialTheme.typography.labelSmall, color = if (elegida) Color.White.copy(alpha = 0.85f) else TintaSuave)
    }
}

// Resultado: varias propuestas que hacen match; el consumidor elige una.
@Composable
fun PropuestasScreen(navController: NavController, appState: AppState) {
    val c = appState.criterios
    val propuestas = appState.obtenerPropuestas(c)
    // Si hay pocas (o ninguna) exactas, sumamos las más parecidas para que siempre haya dónde elegir.
    val yaPropuestos = propuestas.map { Recomendador.restaurante(it.local) }.toSet()
    val alternativas = if (propuestas.size < 3)
        appState.obtenerAlternativas(c).filter { Recomendador.restaurante(it.local) !in yaPropuestos } else emptyList()
    val cuando = c.fecha?.let { textoFechaLarga(it) } ?: nombreDia(c.dia)
    val resumen = "$cuando · %02d:00 · ${c.personas} ${if (c.personas == 1) "persona" else "personas"} · hasta ${c.radioKm.toInt()} km".format(c.hora) +
            if (c.preferencia != TODAS) " · ${c.preferencia}" else ""

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                BarraSuperior(
                    "Propuestas para ti",
                    when {
                        propuestas.isEmpty() -> "Lo más parecido a lo que buscas"
                        propuestas.size == 1 -> "1 propuesta que calza contigo"
                        else -> "${propuestas.size} propuestas que calzan contigo"
                    },
                    icono = Icons.AutoMirrored.Filled.ArrowBack, onIcono = { navController.popBackStack() }
                )
            }
            item {
                AvisoBanner(Icons.Filled.EventAvailable, resumen, "Compara el match y elige la que más te acomode.", Modifier.padding(horizontal = 16.dp)) {
                    TextButton(onClick = { navController.popBackStack() }) {
                        androidx.compose.material3.Icon(Icons.Filled.Edit, null)
                        Text(" Editar")
                    }
                }
            }
            if (propuestas.isEmpty() && alternativas.isEmpty()) {
                item {
                    EstadoVacio(Icons.Filled.SearchOff, "Ningún local calza todavía", "Prueba otra hora o amplía el radio de búsqueda.") {
                        PressButton("Ampliar a 20 km", icono = Icons.Filled.TravelExplore) {
                            appState.criterios = c.copy(radioKm = 20.0)
                        }
                    }
                }
            }
            itemsIndexed(propuestas, key = { _, p -> p.local.id }) { i, p ->
                TarjetaPromo(
                    appState, p.local, i, Modifier.padding(horizontal = 16.dp),
                    match = p.match, razones = p.razones, textoBoton = "Elegir"
                ) { navController.navigate("detalle/${p.local.id}") }
            }
            if (alternativas.isNotEmpty()) {
                item {
                    TituloSeccion(
                        if (propuestas.isEmpty()) "Nada calza exacto, pero mira estas" else "También cerca de lo que buscas",
                        "Mismo día: otra hora, un poco más lejos u otra comida",
                        Modifier.padding(horizontal = 16.dp).padding(top = 6.dp)
                    )
                }
                itemsIndexed(alternativas, key = { _, p -> "alt-${p.local.id}" }) { i, p ->
                    TarjetaPromo(
                        appState, p.local, propuestas.size + i, Modifier.padding(horizontal = 16.dp),
                        match = p.match, razones = p.razones, textoBoton = "Elegir", ajuste = p.ajuste
                    ) { navController.navigate("detalle/${p.local.id}") }
                }
            }
            if (propuestas.isNotEmpty() || alternativas.isNotEmpty()) {
                item {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.Center) {
                        Text(
                            "El % de match suma tu preferencia, tu perfil de consumo, la distancia y el descuento.",
                            style = MaterialTheme.typography.bodySmall, color = TintaSuave,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
