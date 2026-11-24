package cl.dsy1105.cocinacapital.ui.views

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.components.DialogoConsentimiento
import cl.dsy1105.cocinacapital.ui.components.EstiloBoton
import cl.dsy1105.cocinacapital.ui.components.Panel
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.components.TituloSeccion
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.Aqua200
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.PetroleoMedio
import cl.dsy1105.cocinacapital.ui.theme.PetroleoProfundo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.utils.mostrarAlertaPromocion

@Composable
fun PerfilScreen(navController: NavController, appState: AppState) {
    val context = LocalContext.current
    val usuario = appState.usuarioActual
    val prefs = appState.obtenerPreferencias()
    val maximo = (prefs.maxOfOrNull { it.puntaje } ?: 1).toFloat()
    var explicar by remember { mutableStateOf(false) }

    // Alerta predictiva: pide permiso de notificaciones y muestra la mejor promoción.
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) appState.mejorOferta()?.let { mostrarAlertaPromocion(context, it) }
    }

    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Encabezado con avatar
        Surface(shape = RoundedCornerShape(26.dp), color = PetroleoProfundo) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                AvatarCuenta(usuario?.foto, appState.nombreCorto, 76.dp)
                Spacer(Modifier.height(10.dp))
                Text(appState.nombreCorto, style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(usuario?.email ?: "", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.clip(RoundedCornerShape(50)).background(PetroleoMedio).padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(if (appState.esVendedor) Icons.Filled.Storefront else Icons.Filled.ConfirmationNumber, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("  Cuenta ${usuario?.rol ?: ""}", style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth()) {
                    Dato("${appState.obtenerCanjes().size}", "vouchers", Modifier.weight(1f))
                    Dato("${prefs.sumOf { it.puntaje }}", "puntos de perfil", Modifier.weight(1f))
                    Dato("${prefs.size}", "categorías", Modifier.weight(1f))
                }
            }
        }
        PressButton("Editar mi cuenta", Modifier.fillMaxWidth(), icono = Icons.Filled.ManageAccounts, estilo = EstiloBoton.Secundario) {
            navController.navigate("cuenta")
        }

        Panel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Insights, null, tint = Petroleo)
                Spacer(Modifier.width(10.dp))
                TituloSeccion("Tu perfil de consumo", "Sube +1 al revisar un local y +3 al canjear")
            }
            if (prefs.isEmpty()) {
                Text("Revisa locales y canjea beneficios para construir tu perfil.", style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
            }
            prefs.forEach { p ->
                val ancho by animateFloatAsState(p.puntaje / maximo, tween(800), label = "barra")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row {
                        Text(p.categoria, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = Tinta)
                        Text("${p.puntaje} pts", style = MaterialTheme.typography.labelLarge, color = Petroleo)
                    }
                    Box(Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(Aqua100)) {
                        Box(Modifier.fillMaxWidth(ancho).fillMaxHeight().clip(CircleShape).background(Petroleo))
                    }
                }
            }
        }

        Panel {
            TituloSeccion("Alertas predictivas", "Te avisamos la promoción que mejor calza con tu perfil y ubicación")
            PressButton("Probar alerta predictiva", Modifier.fillMaxWidth(), icono = Icons.Filled.NotificationsActive, estilo = EstiloBoton.Secundario) {
                explicar = true
            }
        }

        PressButton("Cerrar sesión", Modifier.fillMaxWidth(), icono = Icons.AutoMirrored.Filled.Logout, estilo = EstiloBoton.Peligro) {
            appState.logout()
            navController.navigate("login") { popUpTo("principal") { inclusive = true } }
        }
    }

    if (explicar) {
        DialogoConsentimiento(
            Icons.Filled.NotificationsActive, "¿Activamos las alertas?",
            "Te enviaremos notificaciones con promociones según tu perfil de consumo. " +
                    "No usamos datos personales reales y puedes desactivarlas cuando quieras.",
            onAceptar = {
                explicar = false
                if (Build.VERSION.SDK_INT >= 33) pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                else appState.mejorOferta()?.let { mostrarAlertaPromocion(context, it) }
            },
            onCancelar = { explicar = false }
        )
    }
}

@Composable
private fun Dato(valor: String, etiqueta: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleLarge, color = Color.White)
        Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.78f))
    }
}
