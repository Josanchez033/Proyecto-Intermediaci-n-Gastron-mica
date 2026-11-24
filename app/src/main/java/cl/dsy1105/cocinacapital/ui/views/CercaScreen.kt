package cl.dsy1105.cocinacapital.ui.views

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.components.AvisoBanner
import cl.dsy1105.cocinacapital.ui.components.BarraSuperior
import cl.dsy1105.cocinacapital.ui.components.DialogoConsentimiento
import cl.dsy1105.cocinacapital.ui.components.TarjetaPromo
import cl.dsy1105.cocinacapital.utils.obtenerUbicacion

@Composable
fun CercaScreen(navController: NavController, appState: AppState) {
    val context = LocalContext.current
    var explicar by remember { mutableStateOf(false) }
    // Pide el permiso de ubicación; si lo conceden, actualiza la posición.
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) obtenerUbicacion(context)?.let { appState.actualizarUbicacion(it.first, it.second) }
    }

    LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { BarraSuperior("Cerca de ti", "Promociones ordenadas por distancia", icono = null) }
        item {
            AvisoBanner(
                Icons.Filled.NearMe,
                if (appState.usandoUbicacionReal) "Ordenadas desde tu ubicación" else "Ordenadas desde ${appState.comunaActual()} centro",
                if (appState.usandoUbicacionReal) "Usamos tu ubicación solo mientras la app está abierta."
                else "Activa tu ubicación para ver distancias reales.",
                Modifier.padding(horizontal = 16.dp)
            ) {
                if (!appState.usandoUbicacionReal) {
                    TextButton(onClick = { explicar = true }) {
                        androidx.compose.material3.Icon(Icons.Filled.MyLocation, null)
                        Text(" Activar")
                    }
                }
            }
        }
        itemsIndexed(appState.obtenerCercanos(), key = { _, l -> l.id }) { i, local ->
            TarjetaPromo(appState, local, i, Modifier.padding(horizontal = 16.dp)) {
                navController.navigate("detalle/${local.id}")
            }
        }
    }

    if (explicar) {
        DialogoConsentimiento(
            Icons.Filled.MyLocation, "¿Usamos tu ubicación?",
            "La usamos solo para ordenar los locales por distancia y sugerirte promociones cercanas. " +
                    "No se guarda ni se comparte, y puedes seguir usando la app si dices que no.",
            onAceptar = { explicar = false; pedirPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
            onCancelar = { explicar = false }
        )
    }
}
