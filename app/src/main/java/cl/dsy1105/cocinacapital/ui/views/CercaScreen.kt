package cl.dsy1105.cocinacapital.ui.views

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.components.EncabezadoDegradado
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.components.TarjetaLocal
import cl.dsy1105.cocinacapital.utils.obtenerUbicacion

@Composable
fun CercaScreen(navController: NavController,
                appState: AppState) {
    val context = LocalContext.current
    // Pide el permiso de ubicación; si lo conceden, actualiza la posición.
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) obtenerUbicacion(context)?.let { appState.actualizarUbicacion(it.first, it.second) }
    }

    Column {
        EncabezadoDegradado("Cerca de ti", "Ordenado por distancia")
        Box(Modifier.padding(16.dp, 12.dp)) {
            PressButton("📍 Usar mi ubicación", Modifier.fillMaxWidth()) {
                pedirPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp, 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(appState.obtenerCercanos()) { i, local ->
                TarjetaLocal(appState, local, i) {
                    navController.navigate("detalle/${local.id}")
                }
            }
        }
    }
}
