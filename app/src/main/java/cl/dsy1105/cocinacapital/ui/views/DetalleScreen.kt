package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Place
import cl.dsy1105.cocinacapital.ui.components.EstiloBoton
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.codigoCanje
import cl.dsy1105.cocinacapital.data.pesos
import cl.dsy1105.cocinacapital.data.room.CanjeEntity
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.textoHorario
import cl.dsy1105.cocinacapital.data.textoVigencia
import cl.dsy1105.cocinacapital.ui.components.Aparecer
import cl.dsy1105.cocinacapital.ui.components.AvisoBanner
import cl.dsy1105.cocinacapital.ui.components.BloquePrecio
import cl.dsy1105.cocinacapital.ui.components.ChipCodigo
import cl.dsy1105.cocinacapital.ui.components.EtiquetaDescuento
import cl.dsy1105.cocinacapital.ui.components.FotoLocal
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.Aqua50
import cl.dsy1105.cocinacapital.ui.theme.AvisoIcono
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.ui.theme.VeloFoto
import cl.dsy1105.cocinacapital.ui.theme.Verde
import cl.dsy1105.cocinacapital.ui.theme.VerdeClaro

@Composable
fun DetalleScreen(navController: NavController, appState: AppState, localId: Long) {
    val local = appState.buscarLocal(localId)
    var confirmar by remember { mutableStateOf(false) }
    var listo by remember { mutableStateOf<CanjeEntity?>(null) }

    // Al abrir el detalle se registra la interacción en el perfil de consumo (+1).
    LaunchedEffect(localId) { local?.let { appState.registrarVista(it) } }

    if (local == null) {
        Box(Modifier.fillMaxSize().background(Aqua50), contentAlignment = Alignment.Center) { Text("Local no encontrado") }
        return
    }
    val canje = appState.canjeDe(local)

    Box(Modifier.fillMaxSize().background(Aqua50)) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth().height(300.dp)) {
                FotoLocal(local.foto, local.nombre, Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize().background(VeloFoto))
                FilledTonalIconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.statusBarsPadding().padding(12.dp).size(48.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White, contentColor = Petroleo)
                ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                EtiquetaDescuento(local.etiquetaOferta, Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 44.dp))
            }
            Aparecer {
                Column(
                    Modifier.offset(y = (-28).dp).clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(Aqua50).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column {
                        Text(local.nombre, style = MaterialTheme.typography.headlineMedium, color = Tinta)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, null, tint = AvisoIcono, modifier = Modifier.size(16.dp))
                            Text(
                                " ${local.rating} · ${local.categoria} · ${local.comuna} · " + "%.1f km".format(appState.distanciaKm(local)).replace(".", ","),
                                style = MaterialTheme.typography.bodyMedium, color = TintaSuave
                            )
                        }
                    }
                    AvisoBanner(
                        Icons.Filled.Schedule, "Válido ${textoHorario(local)}",
                        textoVigencia(local)?.let { "$it · Horario de baja demanda del local" } ?: "Horario de baja demanda del local"
                    )
                    Surface(shape = RoundedCornerShape(22.dp), color = Color.White, shadowElevation = 1.dp) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(local.promocion, style = MaterialTheme.typography.titleLarge, color = Tinta)
                            Text("¿Qué incluye?", style = MaterialTheme.typography.titleSmall, color = Petroleo)
                            Text(local.incluye, style = MaterialTheme.typography.bodyLarge, color = Tinta)
                            HorizontalDivider(color = Aqua100)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Box(Modifier.weight(1f)) { BloquePrecio(local, grande = true) }
                                Text(
                                    "Ahorras ${pesos(local.precioNormal - local.precioPromo)}",
                                    Modifier.clip(RoundedCornerShape(10.dp)).background(VerdeClaro).padding(horizontal = 10.dp, vertical = 6.dp),
                                    color = Verde, style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                    Text(local.descripcion, style = MaterialTheme.typography.bodyLarge, color = Tinta)
                    FilaDato(Icons.Filled.Place, local.direccion + ", " + local.comuna)
                    FilaDato(Icons.Filled.Groups, "Recibe grupos de hasta ${local.capacidad} personas con esta promoción")
                    // Acceso al perfil del restaurante, con todas sus promociones activas.
                    val otras = appState.promocionesDelRestaurante(local).size
                    PressButton(
                        if (otras > 1) "Ver restaurante · $otras promociones" else "Ver perfil del restaurante",
                        Modifier.fillMaxWidth(), icono = Icons.Filled.Storefront, estilo = EstiloBoton.Secundario
                    ) { navController.navigate("restaurante/${local.id}") }
                    Spacer(Modifier.height(96.dp))
                }
            }
        }

        // Barra inferior fija con la acción principal
        Surface(Modifier.align(Alignment.BottomCenter), color = Color.White, shadowElevation = 12.dp) {
            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                if (canje == null) {
                    PressButton("Canjear beneficio", Modifier.fillMaxWidth(), icono = Icons.Filled.LocalOffer) { confirmar = true }
                } else {
                    PressButton("Ver mi voucher", Modifier.fillMaxWidth(), icono = Icons.Filled.QrCode2) {
                        navController.navigate("voucher/${canje.id}")
                    }
                }
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            icon = { Icon(Icons.Filled.ConfirmationNumber, null, tint = Petroleo) },
            title = { Text("Confirmar canje") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Vas a canjear \"${local.promocion}\" en ${local.nombre}.")
                    FilaMonto("Precio normal", pesos(local.precioNormal))
                    FilaMonto("Con tu beneficio", pesos(local.precioPromo), destacado = true)
                    Text("El canje no tiene costo y se usa una sola vez. Pagas en el local.", style = MaterialTheme.typography.bodySmall, color = TintaSuave)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    appState.canjear(local) { listo = it }
                }) { Text("Canjear ahora") }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } },
            containerColor = Color.White
        )
    }

    listo?.let { c ->
        CanjeListoDialogo(local, c, onVer = { listo = null; navController.navigate("voucher/${c.id}") }, onCerrar = { listo = null })
    }
}

@Composable
private fun FilaDato(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).clip(CircleShape).background(Aqua100), contentAlignment = Alignment.Center) {
            Icon(icono, null, tint = Petroleo, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = Tinta)
    }
}

@Composable
fun FilaMonto(etiqueta: String, monto: String, destacado: Boolean = false) {
    Row(Modifier.fillMaxWidth()) {
        Text(etiqueta, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
        Text(monto, style = if (destacado) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (destacado) Verde else Tinta)
    }
}

@Composable
private fun CanjeListoDialogo(local: LocalEntity, canje: CanjeEntity, onVer: () -> Unit, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        icon = { Icon(Icons.Filled.CheckCircle, null, tint = Verde, modifier = Modifier.size(56.dp)) },
        title = { Text("¡Beneficio canjeado!", textAlign = TextAlign.Center) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Tu voucher quedó guardado en Mis vouchers.", textAlign = TextAlign.Center)
                Surface(shape = RoundedCornerShape(16.dp), color = Aqua50) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(local.nombre, style = MaterialTheme.typography.titleMedium, color = Tinta)
                        Text(local.promocion, style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
                        ChipCodigo(codigoCanje(local.id, canje.fecha))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onVer) { Text("Ver voucher") } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Seguir explorando") } },
        containerColor = Color.White
    )
}
