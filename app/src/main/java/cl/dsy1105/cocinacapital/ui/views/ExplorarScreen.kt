package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.components.EncabezadoDegradado
import cl.dsy1105.cocinacapital.ui.components.TarjetaLocal

@Composable
fun ExplorarScreen(navController: NavController,
                   appState: AppState) {
    var busqueda by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Todos") }
    val nombre = appState.usuarioActual?.email?.substringBefore("@") ?: ""

    Column {
        EncabezadoDegradado("Hola, $nombre 👋", "Promociones pensadas para ti")
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            modifier = Modifier.fillMaxWidth().padding(16.dp, 12.dp, 16.dp, 0.dp),
            placeholder = { Text("Busca locales, platos o comunas") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            )
        )
        LazyRow(
            contentPadding = PaddingValues(16.dp, 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(appState.categorias()) { c ->
                FilterChip(selected = categoria == c, onClick = { categoria = c }, label = { Text(c) })
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(appState.obtenerRecomendados(busqueda, categoria)) { i, local ->
                TarjetaLocal(appState, local, i) {
                    navController.navigate("detalle/${local.id}")
                }
            }
        }
    }
}
