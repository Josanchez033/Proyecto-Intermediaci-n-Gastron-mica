package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.TODAS
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.ui.components.BarrasHoras
import cl.dsy1105.cocinacapital.ui.components.EstadoVacio
import cl.dsy1105.cocinacapital.ui.components.FotoLocal
import cl.dsy1105.cocinacapital.ui.components.Panel
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.components.TarjetaPromo
import cl.dsy1105.cocinacapital.ui.components.TarjetaClima
import cl.dsy1105.cocinacapital.ui.components.TituloSeccion
import cl.dsy1105.cocinacapital.viewmodel.ClimaViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.dsy1105.cocinacapital.ui.components.sinAnimaciones
import cl.dsy1105.cocinacapital.utils.inicioDelDia
import cl.dsy1105.cocinacapital.ui.theme.Aqua200
import cl.dsy1105.cocinacapital.ui.theme.Aqua300
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.PetroleoProfundo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

// Inicio del Comprador: la órbita de platos con mesa AHORA (al centro, la hora y
// cuántas mesas hay), la "marea de horas" para armar la salida y el listado "Para ti".
@Composable
fun ExplorarScreen(navController: NavController, appState: AppState, climaVm: ClimaViewModel = viewModel()) {
    // API externa (Open-Meteo): el clima de hoy según la ubicación del usuario.
    LaunchedEffect(appState.latitud, appState.longitud) { climaVm.cargar(appState.latitud, appState.longitud) }
    val clima by climaVm.estado.collectAsState()
    var busqueda by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf(TODAS) }
    val recomendados = appState.obtenerRecomendados(busqueda, categoria)

    val ahora = remember { Calendar.getInstance() }
    val horaActual = ahora.get(Calendar.HOUR_OF_DAY)
    val minutoActual = ahora.get(Calendar.MINUTE)
    val hoy = remember { AppState.criteriosPorDefecto().dia }
    val abiertasAhora = appState.abiertasEn(hoy, horaActual)
    // La marea parte en la hora actual (las horas que ya pasaron no sirven); de noche muestra mañana.
    val esManana = horaActual >= 22
    val diaMarea = if (esManana) hoy % 7 + 1 else hoy
    val marea = appState.mesasPorHora(diaMarea, if (esManana) 8..22 else maxOf(horaActual, 8)..22)
    val proximaHora = if (esManana) null else marea.firstOrNull { (h, n) -> h > horaActual && n > 0 }?.first
    var horaElegida by rememberSaveable {
        mutableIntStateOf(marea.firstOrNull { it.second > 0 }?.first ?: marea.first().first)
    }
    // Seis platos: primero los abiertos ahora, luego los que abren más tarde hoy y después lo mejor para ti.
    val platosDeLaOrbita = (abiertasAhora +
            marea.filter { it.first > horaActual }.flatMap { appState.abiertasEn(hoy, it.first) } +
            appState.obtenerRecomendados("", TODAS)).distinctBy { it.id }.take(6)

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 16.dp, top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Hola, ${appState.nombreCorto}", Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall, color = Tinta)
                Surface(shape = RoundedCornerShape(50), color = Color.White, shadowElevation = 1.dp) {
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Place, null, tint = Petroleo, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(appState.comunaActual(), style = MaterialTheme.typography.labelLarge, color = Tinta)
                    }
                }
            }
        }
        item {
            TarjetaClima(clima, appState.comunaActual(), Modifier.padding(horizontal = 16.dp))
        }
        item {
            OrbitaPlatos(platosDeLaOrbita, horaActual, minutoActual, abiertasAhora.size, proximaHora) {
                navController.navigate("detalle/${it.id}")
            }
        }
        item {
            MareaHoras(
                datos = marea, horaActual = if (esManana) -1 else horaActual, seleccionada = horaElegida, esManana = esManana,
                onSeleccion = { horaElegida = it }, modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                appState.criterios = appState.criterios.copy(
                    dia = diaMarea, hora = horaElegida, fecha = inicioDelDia(if (esManana) 1 else 0)
                )
                navController.navigate("busqueda")
            }
        }
        item {
            Column(Modifier.padding(horizontal = 16.dp).padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TituloSeccion("Para ti", "Ordenado por tu perfil de consumo, la cercanía y el descuento")
                OutlinedTextField(
                    value = busqueda, onValueChange = { busqueda = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Busca locales, platos o comunas") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    trailingIcon = if (busqueda.isNotEmpty()) {
                        { IconButton(onClick = { busqueda = "" }) { Icon(Icons.Filled.Close, "Borrar búsqueda") } }
                    } else null,
                    shape = RoundedCornerShape(18.dp), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Aqua200, focusedBorderColor = Petroleo
                    )
                )
            }
        }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(appState.categorias()) { c ->
                    FilterChip(
                        selected = categoria == c, onClick = { categoria = c }, label = { Text(c) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Petroleo, selectedLabelColor = Color.White, containerColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(true, categoria == c, borderColor = Aqua200)
                    )
                }
            }
        }
        if (recomendados.isEmpty()) {
            item {
                EstadoVacio(Icons.Filled.SearchOff, "No encontramos locales", "Prueba con otra palabra o elige \"Todas\" en las categorías.")
            }
        }
        itemsIndexed(recomendados, key = { _, l -> l.id }) { i, local ->
            val afinidad = appState.afinidad(local.categoria)
            TarjetaPromo(
                appState, local, i, Modifier.padding(horizontal = 16.dp),
                razones = if (afinidad > 0) listOf("Te gusta ${local.categoria.lowercase()}") else emptyList()
            ) { navController.navigate("detalle/${local.id}") }
        }
    }
}

/** Seis platos girando lentamente alrededor de la hora actual y las mesas con descuento disponibles. */
@Composable
private fun OrbitaPlatos(
    locales: List<LocalEntity>, hora: Int, minuto: Int, mesasAhora: Int, proximaHora: Int?,
    onLocal: (LocalEntity) -> Unit
) {
    val sinAnimacion = sinAnimaciones()
    val giro by rememberInfiniteTransition(label = "orbita").animateFloat(
        0f, 360f, infiniteRepeatable(tween(60000, easing = LinearEasing), RepeatMode.Restart), label = "giro"
    )
    val angulo = if (sinAnimacion) 0f else giro
    val radio = 112.dp
    val reloj = "%02d:%02d".format(hora, minuto)
    val resumen = when {
        mesasAhora > 0 -> "Son las $reloj. $mesasAhora ${if (mesasAhora == 1) "mesa" else "mesas"} con descuento ahora."
        proximaHora != null -> "Son las $reloj. Sin mesas ahora; hay desde las %02d:00.".format(proximaHora)
        else -> "Son las $reloj. No quedan mesas con descuento por hoy."
    }

    Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(radio * 2)) {
            drawCircle(
                Aqua300, style = Stroke(2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 12f)))
            )
        }
        Box(
            Modifier.size(128.dp).shadow(8.dp, CircleShape).clip(CircleShape).background(Color.White)
                .semantics(mergeDescendants = true) { contentDescription = resumen },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(reloj, style = MaterialTheme.typography.labelLarge, color = TintaSuave)
                when {
                    mesasAhora > 0 -> {
                        Text("$mesasAhora", style = MaterialTheme.typography.headlineMedium, color = Petroleo)
                        Text(
                            if (mesasAhora == 1) "mesa con\ndescuento" else "mesas con\ndescuento",
                            style = MaterialTheme.typography.labelMedium, color = PetroleoProfundo, textAlign = TextAlign.Center
                        )
                    }
                    proximaHora != null -> {
                        Text("Mesas desde", style = MaterialTheme.typography.labelMedium, color = PetroleoProfundo)
                        Text("%02d:00".format(proximaHora), style = MaterialTheme.typography.headlineSmall, color = Petroleo)
                    }
                    else -> Text("Vuelve\nmañana", style = MaterialTheme.typography.titleSmall, color = PetroleoProfundo, textAlign = TextAlign.Center)
                }
            }
        }
        locales.forEachIndexed { i, local ->
            val a = Math.toRadians((angulo + i * 360f / locales.size - 90f).toDouble())
            val tamano = if (i % 2 == 0) 70.dp else 60.dp
            Box(
                Modifier.offset(radio * cos(a).toFloat(), radio * sin(a).toFloat())
                    .size(tamano).shadow(6.dp, CircleShape).clip(CircleShape)
                    .border(3.dp, Color.White, CircleShape)
                    .clickable(onClickLabel = "Ver ${local.nombre}") { onLocal(local) }
            ) { FotoLocal(local.foto, local.nombre, Modifier.fillMaxSize()) }
        }
    }
}

/**
 * "Marea de horas": cuántas mesas con descuento hay en cada hora desde ahora. Hace visible
 * la idea de Cocina Capital (las horas de baja demanda) y es la entrada a la búsqueda.
 * La frase de arriba lee la barra elegida, así se entiende el gráfico a primera vista.
 */
@Composable
private fun MareaHoras(
    datos: List<Pair<Int, Int>>, horaActual: Int, seleccionada: Int, esManana: Boolean,
    onSeleccion: (Int) -> Unit, modifier: Modifier = Modifier, onArmar: () -> Unit
) {
    val mesasElegida = datos.firstOrNull { it.first == seleccionada }?.second ?: 0
    val hora = "%02d:00".format(seleccionada)

    Panel(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                if (esManana) "¿A qué hora sales mañana?" else "¿A qué hora sales?",
                style = MaterialTheme.typography.titleMedium, color = Tinta
            )
            Text(
                buildAnnotatedString {
                    append("A las $hora ")
                    if (mesasElegida == 0) append("no hay mesas con descuento")
                    else {
                        append("hay ")
                        withStyle(SpanStyle(color = Petroleo, fontWeight = FontWeight.Bold)) {
                            append(if (mesasElegida == 1) "1 mesa con descuento" else "$mesasElegida mesas con descuento")
                        }
                    }
                    append(if (esManana) " mañana." else ".")
                },
                style = MaterialTheme.typography.bodyLarge, color = Tinta
            )
            Text(
                if (esManana) "Por hoy terminaron las promociones. Cada barra es una hora de mañana; toca otra para comparar."
                else "Cada barra es una hora desde ahora: más alta, más mesas. Toca otra para comparar.",
                style = MaterialTheme.typography.bodySmall, color = TintaSuave
            )
        }
        BarrasHoras(
            datos, seleccionada, horaActual,
            unidad = { if (it == 1) "mesa con descuento" else "mesas con descuento" },
            onSeleccion = onSeleccion
        )
        PressButton(
            if (mesasElegida == 0) "Armar mi salida" else "Armar mi salida · ${if (esManana) "mañana" else "hoy"} $hora",
            Modifier.fillMaxWidth(), icono = Icons.Filled.TravelExplore
        ) { onArmar() }
    }
}
