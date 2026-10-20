package cl.dsy1105.cocinacapital.ui.views

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.components.EncabezadoDegradado
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.theme.AzulProfundo
import cl.dsy1105.cocinacapital.ui.theme.DegradadoHero
import cl.dsy1105.cocinacapital.ui.theme.Hielo
import cl.dsy1105.cocinacapital.utils.mostrarAlertaPromocion

@Composable
fun PerfilScreen(navController: NavController,
                 appState: AppState) {
    val context = LocalContext.current
    val usuario = appState.usuarioActual
    val prefs = appState.obtenerPreferencias()
    val maximo = (prefs.maxOfOrNull { it.puntaje } ?: 1).toFloat()

    // Alerta predictiva: pide permiso de notificaciones y muestra la mejor promoción.
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { appState.mejorOferta()?.let { mostrarAlertaPromocion(context, it) } }

    fun cerrarSesion() {
        appState.logout()
        navController.navigate("login") {
            popUpTo("principal") { inclusive = true }
        }
    }

    Column(Modifier.verticalScroll(rememberScrollState())) {
        EncabezadoDegradado(usuario?.email ?: "", "Perfil ${usuario?.rol ?: ""}")
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Tu perfil de consumo (guardado en Room)", fontWeight = FontWeight.Bold, color = AzulProfundo)
            if (prefs.isEmpty()) Text("Explora locales para construir tu perfil.")
            prefs.forEach { p ->
                // barra que se llena con animación
                val ancho by animateFloatAsState(p.puntaje / maximo, tween(800), label = "barra")
                Column {
                    Text("${p.categoria} · ${p.puntaje}", fontSize = 13.sp)
                    Box(Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(Hielo)) {
                        Box(Modifier.fillMaxWidth(ancho).fillMaxHeight().background(DegradadoHero, CircleShape))
                    }
                }
            }
            PressButton("🔔 Probar alerta predictiva", Modifier.fillMaxWidth()) {
                pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            PressButton("Cerrar Sesión", Modifier.fillMaxWidth()) { cerrarSesion() }
        }
    }
}
