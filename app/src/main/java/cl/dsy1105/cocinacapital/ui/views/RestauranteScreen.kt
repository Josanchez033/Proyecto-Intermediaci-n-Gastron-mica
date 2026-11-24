package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.room.RestauranteEntity
import cl.dsy1105.cocinacapital.ui.components.EstadoVacio
import cl.dsy1105.cocinacapital.ui.components.FotoLocal
import cl.dsy1105.cocinacapital.ui.components.Panel
import cl.dsy1105.cocinacapital.ui.components.TarjetaPromo
import cl.dsy1105.cocinacapital.ui.components.TituloSeccion
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.AvisoIcono
import cl.dsy1105.cocinacapital.ui.theme.FondoApp
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.ui.theme.VeloFoto

// Perfil público de un restaurante: lo que el comprador ve al entrar desde una promoción.
@Composable
fun RestauranteScreen(navController: NavController, appState: AppState, localId: Long) {
    val local = appState.buscarLocal(localId)
    Box(Modifier.fillMaxSize().background(FondoApp)) {
        if (local == null) {
            EstadoVacio(Icons.Filled.Storefront, "Restaurante no encontrado", "Puede que haya dejado de publicar promociones.")
            return@Box
        }
        val restaurante = appState.restauranteDe(local)
        val promos = appState.promocionesDelRestaurante(local)
        LazyColumn(contentPadding = PaddingValues(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            perfilRestaurante(appState, restaurante, promos, onVolver = { navController.popBackStack() }) {
                navController.navigate("detalle/${it.id}")
            }
        }
    }
}

/**
 * Contenido del perfil del restaurante (portada, datos y promociones activas).
 * Lo usan el comprador (RestauranteScreen) y el Local en "Mi restaurante", para que
 * el Local vea exactamente lo que ven sus clientes.
 */
fun LazyListScope.perfilRestaurante(
    appState: AppState,
    restaurante: RestauranteEntity,
    promos: List<LocalEntity>,
    onVolver: (() -> Unit)? = null,
    textoBoton: String = "Ver promo",
    debajoPortada: (@Composable () -> Unit)? = null,
    onPromo: (LocalEntity) -> Unit
) {
    item {
        Box(Modifier.fillMaxWidth().height(240.dp)) {
            FotoLocal(restaurante.foto, restaurante.nombre, Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize().background(VeloFoto))
            if (onVolver != null) {
                FilledTonalIconButton(
                    onClick = onVolver, modifier = Modifier.statusBarsPadding().padding(12.dp).size(48.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White, contentColor = Petroleo)
                ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
            }
            Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                Text(restaurante.nombre, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Place, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text(" ${restaurante.comuna}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.92f))
                }
            }
        }
    }
    debajoPortada?.let { contenido -> item { Box(Modifier.padding(horizontal = 16.dp)) { contenido() } } }
    item {
        Panel(Modifier.padding(horizontal = 16.dp)) {
            if (restaurante.descripcion.isNotBlank()) {
                Text(restaurante.descripcion, style = MaterialTheme.typography.bodyLarge, color = Tinta)
            }
            DatoRestaurante(Icons.Filled.Place, "${restaurante.direccion}, ${restaurante.comuna}")
            DatoRestaurante(
                Icons.Filled.LocalOffer,
                if (promos.size == 1) "1 promoción activa" else "${promos.size} promociones activas"
            )
            if (promos.isNotEmpty()) {
                DatoRestaurante(Icons.Filled.Star, "Valoración %.1f".format(promos.map { it.rating }.average()).replace('.', ','), AvisoIcono)
            }
        }
    }
    item { TituloSeccion("Promociones activas", "En sus horas de baja demanda", Modifier.padding(horizontal = 16.dp).padding(top = 4.dp)) }
    if (promos.isEmpty()) {
        item { EstadoVacio(Icons.Filled.LocalOffer, "Sin promociones activas", "Este restaurante no tiene promociones publicadas ahora.") }
    }
    itemsIndexed(promos, key = { _, l -> "perfil-${l.id}" }) { i, l ->
        TarjetaPromo(appState, l, i, Modifier.padding(horizontal = 16.dp), textoBoton = textoBoton) { onPromo(l) }
    }
}

@Composable
private fun DatoRestaurante(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String, tinte: Color = Petroleo) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(34.dp).clip(CircleShape).background(Aqua100), contentAlignment = Alignment.Center) {
            Icon(icono, null, tint = tinte, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
    }
}
