package cl.dsy1105.cocinacapital.ui.views

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.filled.Sell
import cl.dsy1105.cocinacapital.utils.diasDesdeHoy
import cl.dsy1105.cocinacapital.utils.finDeMeses
import cl.dsy1105.cocinacapital.utils.sumarDias
import cl.dsy1105.cocinacapital.utils.inicioDelDia
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.components.CalendarioDialogo
import cl.dsy1105.cocinacapital.data.vecesEnPeriodo
import cl.dsy1105.cocinacapital.data.textoDiasLargo
import cl.dsy1105.cocinacapital.data.plural
import cl.dsy1105.cocinacapital.data.nombreDia
import cl.dsy1105.cocinacapital.data.fechaCorta
import cl.dsy1105.cocinacapital.data.room.Oferta
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.CATEGORIAS_BASE
import cl.dsy1105.cocinacapital.data.FOTOS_DISPONIBLES
import cl.dsy1105.cocinacapital.data.diaCorto
import cl.dsy1105.cocinacapital.data.pesos
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.textoHorario
import cl.dsy1105.cocinacapital.ui.components.Aparecer
import cl.dsy1105.cocinacapital.ui.components.AvisoBanner
import cl.dsy1105.cocinacapital.ui.components.BarraSuperior
import cl.dsy1105.cocinacapital.ui.components.CampoTexto
import cl.dsy1105.cocinacapital.ui.components.Contador
import cl.dsy1105.cocinacapital.ui.components.EstadoVacio
import cl.dsy1105.cocinacapital.ui.components.EtiquetaDescuento
import cl.dsy1105.cocinacapital.ui.components.FilaFlexible
import cl.dsy1105.cocinacapital.ui.components.FotoLocal
import cl.dsy1105.cocinacapital.ui.components.Panel
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.components.TituloSeccion
import cl.dsy1105.cocinacapital.ui.theme.Aqua200
import cl.dsy1105.cocinacapital.ui.theme.FondoApp
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.Rojo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.utils.Campo
import cl.dsy1105.cocinacapital.utils.validarPromocion

// Formulario para crear (id = 0) o editar una promoción.
@Composable
fun PublicarScreen(navController: NavController, appState: AppState, localId: Long) {
    val original = remember(localId) { if (localId == 0L) null else appState.buscarLocal(localId) }
    // Con perfil de restaurante, el nombre viene de ahí (se edita en "Mi restaurante").
    val perfil = remember { appState.miRestaurante() }
    var nombre by remember { mutableStateOf(perfil?.nombre ?: original?.nombre ?: "") }
    var categoria by remember { mutableStateOf(original?.categoria ?: CATEGORIAS_BASE.first()) }
    var promocion by remember { mutableStateOf(original?.promocion ?: "") }
    var incluye by remember { mutableStateOf(original?.incluye ?: "") }
    var precio by remember { mutableStateOf(original?.precioNormal?.toString() ?: "") }
    var descuento by remember { mutableStateOf(original?.descuento?.toString() ?: "30") }
    var dias by remember { mutableStateOf(original?.dias ?: "1234") }
    var inicio by remember { mutableIntStateOf(original?.horaInicio ?: 15) }
    var fin by remember { mutableIntStateOf(original?.horaFin ?: 19) }
    var capacidad by remember { mutableIntStateOf(original?.capacidad ?: 6) }
    var foto by remember { mutableStateOf(original?.foto ?: FOTOS_DISPONIBLES.first()) }
    // Tipo de oferta y programación (desde / hasta). 0 = hoy / sin fecha de término.
    var oferta by remember { mutableIntStateOf(original?.oferta ?: Oferta.PORCENTAJE) }
    // Oferta propia: nombre corto y precio con la promo (el % se calcula solo).
    var etiqueta by remember { mutableStateOf(original?.etiqueta ?: "") }
    var precioPromo by remember {
        mutableStateOf(original?.takeIf { it.oferta == Oferta.PERSONALIZADA }?.precioPromo?.toString() ?: "")
    }
    val hoy = remember { inicioDelDia() }
    var desde by remember { mutableLongStateOf(original?.desde?.takeIf { it > hoy } ?: 0L) }
    var termino by remember { mutableStateOf(if ((original?.hasta ?: 0L) > 0L) Termino.FECHA else Termino.NUNCA) }
    var hastaElegido by remember { mutableLongStateOf(original?.hasta?.takeIf { it >= hoy } ?: sumarDias(hoy, 30)) }
    var calendario by remember { mutableStateOf<String?>(null) } // "desde" o "hasta"
    val inicio0 = if (desde == 0L) hoy else desde
    val hasta = when (termino) {
        Termino.NUNCA -> 0L
        Termino.SEMANA -> sumarDias(inicio0, 6)
        Termino.MES -> finDeMeses(inicio0, 1)
        Termino.TRES_MESES -> finDeMeses(inicio0, 3)
        else -> hastaElegido
    }
    var tocados by remember { mutableStateOf(setOf<String>()) }
    var intento by remember { mutableStateOf(false) }
    val veces = if (hasta > 0L) vecesEnPeriodo(dias, inicio0, hasta) else -1
    val pNormal = precio.toIntOrNull()
    val pPromo = precioPromo.toIntOrNull()
    val descuentoPropio = if (pNormal != null && pNormal > 0 && pPromo != null) ((pNormal - pPromo) * 100 / pNormal).coerceIn(1, 95) else 0
    val descuentoFinal = if (oferta == Oferta.PERSONALIZADA) descuentoPropio else Oferta.descuentoDe(oferta, descuento.toIntOrNull() ?: 0)
    val erroresPropios = if (oferta != Oferta.PERSONALIZADA) emptyMap() else buildMap {
        if (etiqueta.isBlank()) put(Campo.ETIQUETA, "Ponle un nombre corto, ej: Combo")
        if (pPromo == null || pPromo <= 0) put(Campo.PRECIO_PROMO, "Ingresa el precio con la promoción")
        else if (pNormal != null && pPromo >= pNormal) put(Campo.PRECIO_PROMO, "Debe ser menor que el precio normal")
    }
    val errores = validarPromocion(
        nombre, promocion, precio, (if (oferta == Oferta.PERSONALIZADA) 50 else descuentoFinal).toString(), dias, inicio, fin
    ) + erroresPropios +
            when {
                hasta > 0L && hasta < inicio0 -> mapOf(Campo.VIGENCIA to "La fecha de término debe ser después del inicio")
                hasta > 0L && dias.isNotBlank() && veces == 0 -> mapOf(Campo.VIGENCIA to "En ese período no cae ningún día elegido. Amplía las fechas o cambia los días.")
                else -> emptyMap()
            }
    fun error(campo: String) = if (intento || campo in tocados) errores[campo] else null
    var publicado by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            BarraSuperior(
                if (original == null) "Nueva promoción" else "Editar promoción",
                "Los compradores la verán en tu perfil y en las búsquedas",
                icono = Icons.AutoMirrored.Filled.ArrowBack, onIcono = { navController.popBackStack() }
            )
            Column(
                Modifier.weight(1f).imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Panel {
                    TituloSeccion("Foto de la promoción")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(FOTOS_DISPONIBLES) { f ->
                            val sel = f == foto
                            Box(
                                Modifier.size(76.dp).clip(RoundedCornerShape(16.dp))
                                    .border(if (sel) 3.dp else 0.dp, if (sel) Petroleo else Color.Transparent, RoundedCornerShape(16.dp))
                                    .clickable(onClickLabel = "Elegir foto") { foto = f }
                            ) {
                                FotoLocal(f, null, Modifier.fillMaxSize())
                                if (sel) Icon(Icons.Filled.CheckCircle, "Seleccionada", tint = Color.White,
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(20.dp).clip(CircleShape).background(Petroleo))
                            }
                        }
                    }
                }
                Panel {
                    TituloSeccion("La oferta", if (perfil != null) "Se publica como ${perfil.nombre}" else null)
                    if (perfil == null) {
                        CampoTexto(nombre, { nombre = it; tocados = tocados + Campo.NOMBRE }, "Nombre del local", Icons.Filled.Storefront, error(Campo.NOMBRE))
                    }
                    CampoTexto(promocion, { promocion = it; tocados = tocados + Campo.PROMOCION }, "Promoción (ej: 2x1 en pastas)", Icons.Filled.LocalOffer,
                        error(Campo.PROMOCION), valido = promocion.isNotBlank())
                    CampoTexto(incluye, { incluye = it }, "¿Qué incluye? (opcional)", Icons.Filled.RestaurantMenu, lineas = 2)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Category, null, tint = TintaSuave)
                        Spacer(Modifier.width(8.dp))
                        Text("Categoría", style = MaterialTheme.typography.titleSmall, color = Tinta)
                    }
                    FilaFlexible(CATEGORIAS_BASE) { _, c ->
                        FilterChip(
                            selected = categoria == c, onClick = { categoria = c }, label = { Text(c) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Petroleo, selectedLabelColor = Color.White, containerColor = Color.White),
                            border = FilterChipDefaults.filterChipBorder(true, categoria == c, borderColor = Aqua200)
                        )
                    }
                }
                Panel {
                    TituloSeccion("Precio y tipo de oferta")
                    Text(
                        "Elige una de las comunes o arma la tuya: un combo, un menú o un precio especial.",
                        style = MaterialTheme.typography.bodyMedium, color = TintaSuave
                    )
                    FilaFlexible(listOf(
                        Oferta.PORCENTAJE to "% descuento", Oferta.DOS_POR_UNO to "2x1", Oferta.TRES_POR_DOS to "3x2",
                        Oferta.PERSONALIZADA to "Combo o precio especial"
                    )) { _, (o, texto) ->
                            FilterChip(
                                selected = oferta == o, onClick = { oferta = o }, label = { Text(texto) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Petroleo, selectedLabelColor = Color.White, containerColor = Color.White),
                                border = FilterChipDefaults.filterChipBorder(true, oferta == o, borderColor = Aqua200)
                            )
                    }
                    if (oferta == Oferta.PERSONALIZADA) {
                        CampoTexto(
                            etiqueta, { etiqueta = it.take(14); tocados = tocados + Campo.ETIQUETA }, "Nombre de tu oferta", Icons.Filled.Sell,
                            error(Campo.ETIQUETA), valido = etiqueta.isNotBlank(),
                            ayuda = "Se ve en la etiqueta verde. Ej: Combo, Menú, Happy hour (máx. 14)"
                        )
                    }
                    // Hasta 7 dígitos ($9.999.999): evita montos absurdos y desbordes al calcular.
                    CampoTexto(precio, { precio = it.filter(Char::isDigit).take(7); tocados = tocados + Campo.PRECIO }, "Precio normal", Icons.Filled.AttachMoney,
                        error(Campo.PRECIO), teclado = KeyboardType.Number, valido = Campo.PRECIO !in errores)
                    if (oferta == Oferta.PERSONALIZADA) {
                        CampoTexto(
                            precioPromo, { precioPromo = it.filter(Char::isDigit).take(7); tocados = tocados + Campo.PRECIO_PROMO },
                            "Precio con la promoción", Icons.Filled.AttachMoney, error(Campo.PRECIO_PROMO),
                            teclado = KeyboardType.Number, valido = Campo.PRECIO_PROMO !in errores
                        )
                    }
                    if (oferta == Oferta.PORCENTAJE) {
                        CampoTexto(descuento, { descuento = it.filter(Char::isDigit).take(2); tocados = tocados + Campo.DESCUENTO }, "Descuento", Icons.Filled.Percent,
                            error(Campo.DESCUENTO), teclado = KeyboardType.Number, sufijo = "%", valido = Campo.DESCUENTO !in errores,
                            ayuda = "Entre 5% y 90%")
                    }
                    val p = precio.toIntOrNull(); val d = descuento.toIntOrNull()
                    val explicacion = when {
                        p == null || p <= 0 -> null
                        oferta == Oferta.DOS_POR_UNO -> "Llevan 2 y pagan 1: ${pesos(p)} por dos (${pesos(p / 2)} cada uno)"
                        oferta == Oferta.TRES_POR_DOS -> "Llevan 3 y pagan 2: ${pesos(p * 2)} por tres (${pesos(p * 2 / 3)} cada uno)"
                        oferta == Oferta.PERSONALIZADA && pPromo != null && pPromo in 1 until p ->
                            "El comprador paga ${pesos(pPromo)} en vez de ${pesos(p)}: ahorra $descuentoPropio%"
                        oferta == Oferta.PORCENTAJE && d != null && d in 5..90 -> "El comprador paga ${pesos(p * (100 - d) / 100)} en vez de ${pesos(p)}"
                        else -> null
                    }
                    explicacion?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = Petroleo) }
                }
                Panel {
                    TituloSeccion("Horario de baja demanda", "Cuándo quieres llenar el local")
                    FilaFlexible((1..7).toList()) { _, d ->
                        val sel = dias.contains(d.toString())
                        FilterChip(
                            selected = sel,
                            onClick = {
                                dias = if (sel) dias.replace(d.toString(), "") else (dias + d).toList().sorted().joinToString("")
                                tocados = tocados + Campo.DIAS
                            },
                            label = { Text(diaCorto(d)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Petroleo, selectedLabelColor = Color.White, containerColor = Color.White),
                            border = FilterChipDefaults.filterChipBorder(true, sel, borderColor = Aqua200)
                        )
                    }
                    error(Campo.DIAS)?.let { ErrorLinea(it) }
                    Text("Desde", style = MaterialTheme.typography.labelLarge, color = TintaSuave)
                    Contador(inicio, { inicio = it; tocados = tocados + Campo.HORARIO }, 6, 23, ":00 h")
                    Text("Hasta", style = MaterialTheme.typography.labelLarge, color = TintaSuave)
                    Contador(fin, { fin = it; tocados = tocados + Campo.HORARIO }, 7, 24, ":00 h")
                    error(Campo.HORARIO)?.let { ErrorLinea(it) }
                }
                Panel {
                    TituloSeccion("¿Por cuánto tiempo?", "Prográmala una vez y se repite sola cada semana")
                    Text("Empieza", style = MaterialTheme.typography.labelLarge, color = TintaSuave)
                    FilaFlexible(listOf(true, false)) { _, esHoy ->
                        val sel = (desde == 0L) == esHoy
                        FilterChip(
                            selected = sel,
                            onClick = { if (esHoy) desde = 0L else calendario = "desde" },
                            label = { Text(if (esHoy) "Hoy" else if (desde > 0L) "El ${fechaCorta(desde)}" else "Otra fecha") },
                            leadingIcon = if (!esHoy) { { Icon(Icons.Filled.CalendarMonth, null, Modifier.size(18.dp)) } } else null,
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Petroleo, selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White, containerColor = Color.White),
                            border = FilterChipDefaults.filterChipBorder(true, sel, borderColor = Aqua200)
                        )
                    }
                    Text("Termina", style = MaterialTheme.typography.labelLarge, color = TintaSuave)
                    FilaFlexible(Termino.entries) { _, t ->
                        val sel = termino == t
                        FilterChip(
                            selected = sel,
                            onClick = { if (t == Termino.FECHA) calendario = "hasta" else termino = t },
                            label = { Text(if (t == Termino.FECHA && sel) "El ${fechaCorta(hastaElegido)}" else t.texto) },
                            leadingIcon = if (t == Termino.FECHA) { { Icon(Icons.Filled.CalendarMonth, null, Modifier.size(18.dp)) } } else null,
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Petroleo, selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White, containerColor = Color.White),
                            border = FilterChipDefaults.filterChipBorder(true, sel, borderColor = Aqua200)
                        )
                    }
                    ResumenProgramacion(
                        when (oferta) {
                            Oferta.DOS_POR_UNO -> "2x1"
                            Oferta.TRES_POR_DOS -> "3x2"
                            Oferta.PERSONALIZADA -> etiqueta.ifBlank { "Tu oferta" }
                            else -> "${descuento.toIntOrNull() ?: 0}% de descuento"
                        },
                        dias, inicio, fin, desde, hasta, veces
                    )
                    errores[Campo.VIGENCIA]?.let { ErrorLinea(it) }
                }
                Panel {
                    TituloSeccion("¿Para cuántas personas?", "Cupo máximo por grupo con esta promoción")
                    Contador(capacidad, { capacidad = it }, 1, 30, "personas")
                }
                if (publicado) {
                    AvisoBanner(Icons.Filled.CheckCircle, "Promoción guardada", "Ya aparece en tu perfil y en las búsquedas.")
                }
                Spacer(Modifier.height(8.dp))
            }
            Surface(color = Color.White, shadowElevation = 12.dp) {
                Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                    PressButton(if (original == null) "Publicar promoción" else "Guardar cambios", Modifier.fillMaxWidth(), icono = Icons.Filled.Place) {
                        intento = true
                        if (errores.isEmpty()) {
                            val base = original ?: LocalEntity(
                                nombre = "", categoria = "", comuna = appState.comunaActual(), direccion = "Dirección por confirmar",
                                promocion = "", incluye = "", descripcion = "Promoción publicada por el local.",
                                descuento = 0, precioNormal = 0, dias = "", horaInicio = 0, horaFin = 0, capacidad = 0,
                                rating = 5.0, latitud = 0.0, longitud = 0.0, foto = foto
                            )
                            appState.guardarPromocion(
                                base.copy(
                                    nombre = nombre.trim(), categoria = categoria, promocion = promocion.trim(),
                                    incluye = incluye.trim().ifBlank { promocion.trim() },
                                    precioNormal = precio.toInt(), descuento = descuentoFinal,
                                    dias = dias, horaInicio = inicio, horaFin = fin, capacidad = capacidad, foto = foto,
                                    oferta = oferta, desde = desde, hasta = hasta,
                                    etiqueta = if (oferta == Oferta.PERSONALIZADA) etiqueta.trim() else "",
                                    precioOferta = if (oferta == Oferta.PERSONALIZADA) pPromo ?: 0 else 0
                                )
                            )
                            publicado = true
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
        calendario?.let { cual ->
            CalendarioDialogo(
                if (cual == "desde") "¿Desde qué día?" else "¿Hasta qué día?",
                if (cual == "desde") inicio0 else hasta.takeIf { it > 0L } ?: hastaElegido,
                primerDia = if (cual == "desde") 0 else diasDesdeHoy(inicio0).coerceAtLeast(0),
                ultimoDia = 730, onCerrar = { calendario = null }
            ) { f ->
                if (cual == "desde") desde = if (f <= hoy) 0L else f
                else { hastaElegido = f; termino = Termino.FECHA }
            }
        }
    }
}

/** Opciones de término de una promoción programada. */
private enum class Termino(val texto: String) {
    NUNCA("Hasta que la pause"), SEMANA("1 semana"), MES("1 mes"), TRES_MESES("3 meses"), FECHA("Otra fecha")
}

/**
 * La programación en una frase, para que el Local confirme qué va a ver el comprador:
 * "2x1 todos los viernes, de 19:00 a 23:00. Desde hoy hasta el dom 21 dic: 4 viernes."
 */
@Composable
private fun ResumenProgramacion(
    queOferta: String, dias: String, inicio: Int, fin: Int, desde: Long, hasta: Long, veces: Int
) {
    if (dias.isBlank() || fin <= inicio) return
    val nums = dias.mapNotNull { it.digitToIntOrNull() }
    // Las horas se formatean aparte: el texto de la oferta puede traer "%" (ej. "30% de descuento").
    val cuando = "$queOferta ${textoDiasLargo(dias)}, de " + "%02d:00 a %02d:00.".format(inicio, fin)
    val periodo = when {
        hasta == 0L && desde == 0L -> "Empieza hoy y sigue hasta que la pauses."
        hasta == 0L -> "Empieza el ${fechaCorta(desde)} y sigue hasta que la pauses."
        else -> {
            val rango = if (desde == 0L) "Desde hoy hasta el ${fechaCorta(hasta)}" else "Del ${fechaCorta(desde)} al ${fechaCorta(hasta)}"
            val unidad = when {
                nums.size == 1 && veces == 1 -> nombreDia(nums.first()).lowercase()
                nums.size == 1 -> plural(nums.first())
                veces == 1 -> "día de promoción"
                else -> "días de promoción"
            }
            if (veces > 0) "$rango: $veces $unidad. Después pasa sola a tu historial." else "$rango."
        }
    }
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Aqua100).padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Filled.EventRepeat, null, tint = Petroleo, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(cuando.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleSmall, color = Tinta)
            Text(periodo, style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
        }
    }
}

@Composable
private fun ErrorLinea(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.ErrorOutline, null, tint = Rojo, modifier = Modifier.size(16.dp))
        Text(" $texto", color = Rojo, style = MaterialTheme.typography.bodySmall)
    }
}

