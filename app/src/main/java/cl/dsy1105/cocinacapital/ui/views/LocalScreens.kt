package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import cl.dsy1105.cocinacapital.data.textoVigencia
import cl.dsy1105.cocinacapital.data.programada
import cl.dsy1105.cocinacapital.data.terminada
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.FOTOS_DISPONIBLES
import cl.dsy1105.cocinacapital.data.pesos
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.room.Oferta
import cl.dsy1105.cocinacapital.data.room.RestauranteEntity
import cl.dsy1105.cocinacapital.data.textoHorario
import cl.dsy1105.cocinacapital.ui.components.Aparecer
import cl.dsy1105.cocinacapital.ui.components.AvisoBanner
import cl.dsy1105.cocinacapital.ui.components.BarraSuperior
import cl.dsy1105.cocinacapital.ui.components.CampoTexto
import cl.dsy1105.cocinacapital.ui.components.EstadoVacio
import cl.dsy1105.cocinacapital.ui.components.EstiloBoton
import cl.dsy1105.cocinacapital.ui.components.EtiquetaDescuento
import cl.dsy1105.cocinacapital.ui.components.FilaFlexible
import cl.dsy1105.cocinacapital.ui.components.FotoLocal
import cl.dsy1105.cocinacapital.ui.components.Panel
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.components.TituloSeccion
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.Aqua200
import cl.dsy1105.cocinacapital.ui.theme.FondoApp
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.PetroleoMedio
import cl.dsy1105.cocinacapital.ui.theme.PetroleoProfundo
import cl.dsy1105.cocinacapital.ui.theme.Rojo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.ui.theme.Verde
import cl.dsy1105.cocinacapital.utils.Campo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val COMUNAS_LOCAL = listOf("Valparaíso", "Viña del Mar", "Concón", "Quilpué", "Villa Alemana")

private data class PestanaLocal(val nombre: String, val activo: ImageVector, val inactivo: ImageVector)

/**
 * App del Local (Vendedor): separada de la del comprador. No ve otros locales;
 * solo su restaurante, sus promociones (activas e historial) y su cuenta.
 */
@Composable
fun LocalPrincipalScreen(navController: NavController, appState: AppState) {
    val pestanas = listOf(
        PestanaLocal("Mi restaurante", Icons.Filled.Storefront, Icons.Outlined.Storefront),
        PestanaLocal("Promociones", Icons.Filled.LocalOffer, Icons.Outlined.LocalOffer),
        PestanaLocal("Cuenta", Icons.Filled.Person, Icons.Outlined.Person)
    )
    var seleccionada by rememberSaveable { mutableIntStateOf(0) }

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                    pestanas.forEachIndexed { i, p ->
                        NavigationBarItem(
                            selected = seleccionada == i, onClick = { seleccionada = i },
                            icon = { Icon(if (seleccionada == i) p.activo else p.inactivo, null) },
                            label = { Text(p.nombre) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Aqua200,
                                selectedIconColor = PetroleoProfundo, selectedTextColor = PetroleoProfundo,
                                unselectedIconColor = TintaSuave, unselectedTextColor = TintaSuave
                            )
                        )
                    }
                }
            }
        ) { padding ->
            AnimatedContent(
                seleccionada, Modifier.padding(padding),
                transitionSpec = { fadeIn(tween(220, 90)) togetherWith fadeOut(tween(90)) }, label = "pestanasLocal"
            ) { i ->
                when (i) {
                    0 -> MiRestauranteTab(navController, appState)
                    1 -> PromocionesLocalTab(navController, appState)
                    else -> CuentaLocalTab(navController, appState)
                }
            }
        }
    }
}

/** Perfil del restaurante tal como lo ven los compradores, con acceso a editarlo. */
@Composable
private fun MiRestauranteTab(navController: NavController, appState: AppState) {
    val perfil = appState.miRestaurante()
    val activas = appState.misPromociones().filter { it.activa }
    val vista = perfil ?: RestauranteEntity(
        appState.usuarioActual?.email ?: "", "Tu restaurante", "Sin comuna", "Sin dirección", "", "foto_salon"
    )
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        perfilRestaurante(
            appState, vista, activas, textoBoton = "Editar",
            debajoPortada = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (perfil == null) {
                        AvisoBanner(Icons.Filled.Storefront, "Completa el perfil de tu restaurante", "Así los compradores saben quién eres y dónde estás.")
                    } else {
                        AvisoBanner(Icons.Filled.Visibility, "Así te ven los compradores", "Toca una promoción para editarla.")
                    }
                    PressButton(
                        if (perfil == null) "Completar perfil" else "Editar perfil del restaurante",
                        Modifier.fillMaxWidth(), icono = Icons.Filled.Edit, estilo = EstiloBoton.Secundario
                    ) { navController.navigate("mi-restaurante/editar") }
                }
            }
        ) { navController.navigate("publicar/${it.id}") }
    }
}

/** Promociones del Local: activas (visibles) e historial (pausadas), con sus canjes. */
@Composable
private fun PromocionesLocalTab(navController: NavController, appState: AppState) {
    val todas = appState.misPromociones()
    // Activas incluye las programadas (aún no empiezan). Al historial van las pausadas y las que ya terminaron.
    val activas = todas.filter { it.activa && !terminada(it) }
    val historial = todas.filter { !it.activa || terminada(it) }
    var verHistorial by rememberSaveable { mutableStateOf(false) }
    var porEliminar by remember { mutableStateOf<LocalEntity?>(null) }
    val lista = if (verHistorial) historial else activas

    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { BarraSuperior("Promociones", "Llena tus horas de baja demanda", icono = null) }
        item {
            PressButton("Nueva promoción", Modifier.fillMaxWidth().padding(horizontal = 16.dp), icono = Icons.Filled.Add) {
                navController.navigate("publicar/0")
            }
        }
        item {
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(false to "Activas (${activas.size})", true to "Historial (${historial.size})").forEach { (h, texto) ->
                    FilterChip(
                        selected = verHistorial == h, onClick = { verHistorial = h }, label = { Text(texto) },
                        leadingIcon = { Icon(if (h) Icons.Filled.History else Icons.Filled.LocalOffer, null, Modifier.size(18.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Petroleo, selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White, containerColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(true, verHistorial == h, borderColor = Aqua200)
                    )
                }
            }
        }
        if (lista.isEmpty()) {
            item {
                if (verHistorial) EstadoVacio(Icons.Filled.History, "Tu historial está vacío", "Las promociones que pauses o que terminen su período quedan aquí para reactivarlas o renovarlas.")
                else EstadoVacio(Icons.Filled.Storefront, "No tienes promociones activas", "Publica una oferta para tus horarios vacíos y la verán los compradores.")
            }
        }
        itemsIndexed(lista, key = { _, l -> l.id }) { i, local ->
            Aparecer(i * 60) {
                TarjetaPromoLocal(
                    local, appState.canjesDe(local.id), Modifier.padding(horizontal = 16.dp),
                    onEditar = { navController.navigate("publicar/${local.id}") },
                    onCambiarEstado = {
                        // Una promoción que ya terminó se renueva eligiendo nuevas fechas.
                        if (terminada(local)) navController.navigate("publicar/${local.id}")
                        else appState.cambiarEstadoPromocion(local, !local.activa)
                    },
                    onEliminar = { porEliminar = local }
                )
            }
        }
    }

    porEliminar?.let { local ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            icon = { Icon(Icons.Filled.DeleteForever, null, tint = Rojo) },
            title = { Text("¿Eliminar promoción?") },
            text = { Text("\"${local.promocion}\" se borrará también de tu historial. Si solo quieres dejar de mostrarla, usa Pausar.") },
            confirmButton = { TextButton(onClick = { appState.eliminarPromocion(local); porEliminar = null }) { Text("Eliminar", color = Rojo) } },
            dismissButton = { TextButton(onClick = { porEliminar = null }) { Text("Cancelar") } },
            containerColor = Color.White
        )
    }
}

private val FORMATO_FECHA = SimpleDateFormat("d MMM yyyy", Locale("es", "CL"))

@Composable
private fun TarjetaPromoLocal(
    local: LocalEntity, canjes: Int, modifier: Modifier = Modifier,
    onEditar: () -> Unit, onCambiarEstado: () -> Unit, onEliminar: () -> Unit
) {
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = Color.White, shadowElevation = 1.dp) {
        Column {
            Box(Modifier.fillMaxWidth().height(110.dp)) {
                FotoLocal(local.foto, null, Modifier.fillMaxSize())
                val termino = terminada(local)
                val (estado, colorEstado) = when {
                    !local.activa -> "Pausada" to TintaSuave
                    termino -> "Terminó" to TintaSuave
                    programada(local) -> "Programada" to Petroleo
                    else -> "Activa" to Verde
                }
                if (!local.activa || termino) Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.55f)))
                EtiquetaDescuento(local.etiquetaOferta, Modifier.padding(12.dp))
                Text(
                    estado,
                    Modifier.align(Alignment.TopEnd).padding(12.dp).clip(RoundedCornerShape(50))
                        .background(colorEstado).padding(horizontal = 10.dp, vertical = 4.dp),
                    color = Color.White, style = MaterialTheme.typography.labelMedium
                )
            }
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(local.promocion, style = MaterialTheme.typography.titleMedium, color = Tinta)
                Text("${textoHorario(local)} · hasta ${local.capacidad} personas", style = MaterialTheme.typography.bodySmall, color = TintaSuave)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.EventRepeat, null, tint = Petroleo, modifier = Modifier.size(16.dp))
                    Text(
                        " " + (textoVigencia(local) ?: "Sin fecha de término: sigue hasta que la pauses"),
                        style = MaterialTheme.typography.bodySmall, color = Petroleo
                    )
                }
                Text(
                    when (local.oferta) {
                        Oferta.DOS_POR_UNO -> "Llevan 2 y pagan 1 · ${pesos(local.precioNormal)} cada uno"
                        Oferta.TRES_POR_DOS -> "Llevan 3 y pagan 2 · ${pesos(local.precioNormal)} cada uno"
                        Oferta.PERSONALIZADA -> "${local.etiqueta} · ${pesos(local.precioNormal)} → ${pesos(local.precioPromo)}"
                        else -> "${pesos(local.precioNormal)} → ${pesos(local.precioPromo)}"
                    },
                    style = MaterialTheme.typography.labelLarge, color = Petroleo
                )
                Text(
                    (if (canjes == 1) "1 canje" else "$canjes canjes") +
                            if (local.creada > 0) " · publicada el ${FORMATO_FECHA.format(Date(local.creada))}" else "",
                    style = MaterialTheme.typography.bodySmall, color = TintaSuave
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 6.dp)) {
                    TextButton(onClick = onEditar) { Icon(Icons.Filled.Edit, null); Text(" Editar") }
                    TextButton(onClick = onCambiarEstado) {
                        when {
                            terminada(local) -> { Icon(Icons.Filled.EventRepeat, null); Text(" Renovar") }
                            local.activa -> { Icon(Icons.Filled.Pause, null); Text(" Pausar") }
                            else -> { Icon(Icons.Filled.PlayArrow, null); Text(" Reactivar") }
                        }
                    }
                    TextButton(onClick = onEliminar) { Icon(Icons.Filled.Delete, null, tint = Rojo); Text(" Eliminar", color = Rojo) }
                }
            }
        }
    }
}

/** Cuenta del Local: datos de acceso, resumen y cierre de sesión. */
@Composable
private fun CuentaLocalTab(navController: NavController, appState: AppState) {
    val perfil = appState.miRestaurante()
    val promos = appState.misPromociones()
    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(shape = RoundedCornerShape(26.dp), color = PetroleoProfundo) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                AvatarCuenta(appState.usuarioActual?.foto, perfil?.nombre ?: "", 76.dp, iconoSinFoto = Icons.Filled.Storefront)
                Spacer(Modifier.height(10.dp))
                Text(perfil?.nombre ?: "Tu restaurante", style = MaterialTheme.typography.titleLarge, color = Color.White)
                val encargado = appState.usuarioActual?.nombre.orEmpty()
                Text(
                    if (encargado.isNotBlank()) "$encargado · ${appState.usuarioActual?.email}" else appState.usuarioActual?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.clip(RoundedCornerShape(50)).background(PetroleoMedio).padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Verified, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("  Local verificado por Cocina Capital", style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth()) {
                    DatoCuenta("${promos.count { it.activa }}", "activas", Modifier.weight(1f))
                    DatoCuenta("${promos.count { !it.activa }}", "en historial", Modifier.weight(1f))
                    DatoCuenta("${promos.sumOf { appState.canjesDe(it.id) }}", "canjes", Modifier.weight(1f))
                }
            }
        }
        Panel {
            TituloSeccion("Tu cuenta", "Creada por el administrador de Cocina Capital")
            Text(
                "Cambia tu foto, tu nombre, el correo de acceso o la contraseña. Si olvidas la contraseña o tu cuenta fue desactivada, el administrador te ayuda.",
                style = MaterialTheme.typography.bodyMedium, color = TintaSuave
            )
            PressButton("Editar mi cuenta", Modifier.fillMaxWidth(), icono = Icons.Filled.ManageAccounts, estilo = EstiloBoton.Secundario) {
                navController.navigate("cuenta")
            }
        }
        PressButton("Cerrar sesión", Modifier.fillMaxWidth(), icono = Icons.AutoMirrored.Filled.Logout, estilo = EstiloBoton.Peligro) {
            appState.logout()
            navController.navigate("login") { popUpTo("principal") { inclusive = true } }
        }
    }
}

@Composable
private fun DatoCuenta(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleLarge, color = Color.White)
        Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
    }
}

/** El Local edita su perfil: nombre, foto, comuna, dirección y descripción. */
@Composable
fun EditarRestauranteScreen(navController: NavController, appState: AppState) {
    val perfil = remember { appState.miRestaurante() }
    var nombre by rememberSaveable { mutableStateOf(perfil?.nombre ?: "") }
    var comuna by rememberSaveable { mutableStateOf(perfil?.comuna ?: "") }
    var direccion by rememberSaveable { mutableStateOf(perfil?.direccion ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(perfil?.descripcion ?: "") }
    var foto by rememberSaveable { mutableStateOf(perfil?.foto ?: "foto_salon") }
    var tocados by rememberSaveable { mutableStateOf(setOf<String>()) }
    var intento by rememberSaveable { mutableStateOf(false) }

    val errores = buildMap {
        if (nombre.isBlank()) put(Campo.NOMBRE, "El nombre del restaurante es obligatorio")
        if (direccion.isBlank()) put(Campo.DIRECCION, "Indica la dirección del local")
        if (comuna.isBlank()) put(Campo.COMUNA, "Elige la comuna")
        if (descripcion.length > 160) put(Campo.PROMOCION, "Máximo 160 caracteres (${descripcion.length}/160)")
    }
    fun error(c: String) = if (intento || c in tocados) errores[c] else null

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            BarraSuperior("Perfil del restaurante", "Lo que ven los compradores", onIcono = { navController.popBackStack() })
            Column(
                Modifier.weight(1f).imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Panel {
                    TituloSeccion("Foto de portada")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(listOf("foto_salon", "foto_terraza", "foto_mesa") + FOTOS_DISPONIBLES) { f ->
                            val sel = f == foto
                            Box(
                                Modifier.size(76.dp).clip(RoundedCornerShape(16.dp))
                                    .border(if (sel) 3.dp else 0.dp, if (sel) Petroleo else Color.Transparent, RoundedCornerShape(16.dp))
                                    .clickable(onClickLabel = "Elegir foto de portada") { foto = f }
                            ) {
                                FotoLocal(f, null, Modifier.fillMaxSize())
                                if (sel) Icon(Icons.Filled.CheckCircle, "Seleccionada", tint = Color.White,
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(20.dp).clip(CircleShape).background(Petroleo))
                            }
                        }
                    }
                }
                Panel {
                    TituloSeccion("Datos del restaurante")
                    CampoTexto(nombre, { nombre = it; tocados = tocados + Campo.NOMBRE }, "Nombre del restaurante", Icons.Filled.Storefront, error(Campo.NOMBRE))
                    CampoTexto(direccion, { direccion = it; tocados = tocados + Campo.DIRECCION }, "Dirección", Icons.Filled.Place, error(Campo.DIRECCION))
                    Text("Comuna", style = MaterialTheme.typography.titleSmall, color = Tinta)
                    FilaFlexible(COMUNAS_LOCAL) { _, c ->
                        FilterChip(
                            selected = comuna == c, onClick = { comuna = c; tocados = tocados + Campo.COMUNA }, label = { Text(c) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Petroleo, selectedLabelColor = Color.White, containerColor = Color.White),
                            border = FilterChipDefaults.filterChipBorder(true, comuna == c, borderColor = Aqua200)
                        )
                    }
                    error(Campo.COMUNA)?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Rojo) }
                    CampoTexto(
                        descripcion, { descripcion = it; tocados = tocados + Campo.PROMOCION }, "Descripción (${descripcion.length}/160)",
                        Icons.Filled.Description, error(Campo.PROMOCION), lineas = 3
                    )
                }
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Aqua100).padding(12.dp)) {
                    Text(
                        "Tus promociones se actualizan con el nuevo nombre, dirección y descripción.",
                        style = MaterialTheme.typography.bodySmall, color = PetroleoProfundo
                    )
                }
            }
            Surface(color = Color.White, shadowElevation = 12.dp) {
                PressButton(
                    "Guardar perfil", Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), icono = Icons.Filled.CheckCircle
                ) {
                    intento = true
                    if (errores.isEmpty()) {
                        appState.guardarRestaurante(nombre, comuna, direccion, descripcion, foto)
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
