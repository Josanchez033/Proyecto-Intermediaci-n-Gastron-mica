package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.codigoCanje
import cl.dsy1105.cocinacapital.data.pesos
import cl.dsy1105.cocinacapital.data.textoHorario
import cl.dsy1105.cocinacapital.ui.components.Aparecer
import cl.dsy1105.cocinacapital.ui.components.AvisoBanner
import cl.dsy1105.cocinacapital.ui.components.BarraSuperior
import cl.dsy1105.cocinacapital.ui.components.ChipCodigo
import cl.dsy1105.cocinacapital.ui.components.CodigoQR
import cl.dsy1105.cocinacapital.ui.components.EstadoVacio
import cl.dsy1105.cocinacapital.ui.components.FotoLocal
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.Aqua50
import cl.dsy1105.cocinacapital.ui.theme.FondoApp
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.PetroleoProfundo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.ui.theme.Verde
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Pestaña "Vouchers": beneficios canjeados por el usuario.
@Composable
fun BeneficiosScreen(navController: NavController, appState: AppState, onExplorar: () -> Unit) {
    val canjes = appState.obtenerCanjes()
    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            BarraSuperior(
                "Mis vouchers",
                if (canjes.size == 1) "1 beneficio activo" else "${canjes.size} beneficios activos",
                icono = null
            )
        }
        if (canjes.isEmpty()) {
            item {
                EstadoVacio(
                    Icons.Filled.ConfirmationNumber, "Aún no has canjeado beneficios",
                    "Cuando canjees una promoción, tu voucher con código QR aparecerá aquí."
                ) { PressButton("Explorar promociones", onClick = onExplorar) }
            }
        }
        itemsIndexed(canjes, key = { _, it -> it.first.id }) { i, (canje, local) ->
            Aparecer(i * 60) {
                Surface(
                    Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                        .clickable(onClickLabel = "Abrir voucher de ${local.nombre}") { navController.navigate("voucher/${canje.id}") },
                    shape = RoundedCornerShape(22.dp), color = Color.White, shadowElevation = 1.dp
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        FotoLocal(local.foto, null, Modifier.size(60.dp).clip(CircleShape))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(local.nombre, style = MaterialTheme.typography.titleMedium, color = Tinta)
                            Text(local.promocion, style = MaterialTheme.typography.bodySmall, color = TintaSuave)
                            ChipCodigo(codigoCanje(local.id, canje.fecha))
                        }
                        Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Aqua100), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.QrCode2, "Ver QR", tint = Petroleo)
                        }
                    }
                }
            }
        }
    }
}

// Detalle de un voucher: el código y QR que se muestran en el local.
@Composable
fun VoucherScreen(navController: NavController, appState: AppState, canjeId: Long) {
    val dato = appState.buscarCanje(canjeId)
    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())) {
            BarraSuperior("Voucher", icono = Icons.AutoMirrored.Filled.ArrowBack, onIcono = { navController.popBackStack() })
            if (dato == null) {
                EstadoVacio(Icons.Filled.ConfirmationNumber, "Voucher no encontrado", "Vuelve a Mis vouchers para verlo.")
                return@Column
            }
            val (canje, local) = dato
            val codigo = codigoCanje(local.id, canje.fecha)
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(shape = RoundedCornerShape(24.dp), color = PetroleoProfundo) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        FotoLocal(local.foto, null, Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(local.nombre, style = MaterialTheme.typography.titleLarge, color = Color.White)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Place, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                                Text(" ${local.direccion}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                            }
                        }
                        Text(
                            "Activo", Modifier.clip(RoundedCornerShape(50)).background(Verde).padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.White, style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Surface(shape = RoundedCornerShape(24.dp), color = Color.White, shadowElevation = 2.dp) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Muestra este código en el local", style = MaterialTheme.typography.titleMedium, color = Tinta)
                        Text("El personal lo escanea o digita el código.", style = MaterialTheme.typography.bodySmall, color = TintaSuave, textAlign = TextAlign.Center)
                        CodigoQR("COCINACAPITAL|$codigo|${local.id}", 200.dp)
                        Text("Código de canje", style = MaterialTheme.typography.labelMedium, color = TintaSuave)
                        Text(codigo, style = MaterialTheme.typography.headlineSmall, color = Tinta, letterSpacing = androidx.compose.ui.unit.TextUnit(2f, androidx.compose.ui.unit.TextUnitType.Sp))
                    }
                }
                AvisoBanner(Icons.Filled.Schedule, "Úsalo ${textoHorario(local)}", "Fuera de ese horario el local puede no aceptarlo.")
                Surface(shape = RoundedCornerShape(22.dp), color = Color.White, shadowElevation = 1.dp) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Detalle del beneficio", style = MaterialTheme.typography.titleSmall, color = Petroleo)
                        Text(local.promocion, style = MaterialTheme.typography.bodyLarge, color = Tinta)
                        HorizontalDivider(color = Aqua100)
                        FilaMonto("Precio normal", pesos(local.precioNormal))
                        FilaMonto("Con tu beneficio", pesos(local.precioPromo), destacado = true)
                        FilaMonto("Canjeado el", SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("es", "CL")).format(Date(canje.fecha)))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
