package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.components.Aparecer
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.theme.AzulPrimario
import cl.dsy1105.cocinacapital.ui.theme.AzulProfundo
import cl.dsy1105.cocinacapital.ui.theme.DegradadoFondo
import cl.dsy1105.cocinacapital.ui.theme.DegradadoHero

@Composable
fun DetalleScreen(navController: NavController,
                  appState: AppState,
                  localId: Long) {
    val local = appState.buscarLocal(localId)

    // Al abrir el detalle se registra la interacción en el perfil de consumo.
    LaunchedEffect(localId) { local?.let { appState.registrarVista(it) } }

    Scaffold(containerColor = Color.Transparent, modifier = Modifier.background(DegradadoFondo)) { padding ->
        if (local == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("Local no encontrado") }
            return@Scaffold
        }
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(
                Modifier.fillMaxWidth().height(230.dp)
                    .background(DegradadoHero, RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            ) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(top = padding.calculateTopPadding(), start = 8.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                }
                Text(local.emoji, fontSize = 90.sp, modifier = Modifier.align(Alignment.Center))
            }
            Aparecer {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(local.nombre, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = AzulProfundo)
                    Text("${local.categoria} · ${local.comuna} · ★ ${local.rating} · %.1f km".format(appState.distanciaKm(local)), color = AzulPrimario)
                    Text(local.descripcion)
                    Text("🎁 ${local.promocion}", fontWeight = FontWeight.Bold)
                    Text("🕒 ${local.horario}")
                    val canjeado = appState.yaCanjeado(local)
                    PressButton(
                        if (canjeado) "✅ Beneficio canjeado" else "Canjear beneficio -${local.descuento}%",
                        Modifier.fillMaxWidth()
                    ) { if (!canjeado) appState.canjear(local) }
                }
            }
        }
    }
}
