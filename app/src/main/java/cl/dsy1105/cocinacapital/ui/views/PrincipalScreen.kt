package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.theme.DegradadoFondo

// Pantalla contenedora: barra inferior con pestañas animadas.
// El vendedor ve una pestaña extra ("Publicar").
@Composable
fun PrincipalScreen(navController: NavController,
                    appState: AppState) {
    val esVendedor = appState.usuarioActual?.rol == "Vendedor"
    val pestanas = buildList {
        add("Explorar" to Icons.Default.Home)
        add("Cerca" to Icons.Default.LocationOn)
        add("Beneficios" to Icons.Default.Favorite)
        if (esVendedor) add("Publicar" to Icons.Default.Add)
        add("Perfil" to Icons.Default.Person)
    }
    var seleccionada by remember { mutableIntStateOf(0) }

    Box(Modifier.fillMaxSize().background(DegradadoFondo)) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(containerColor = Color.White.copy(alpha = 0.92f)) {
                    pestanas.forEachIndexed { i, (nombre, icono) ->
                        // el ícono activo "salta" con un resorte
                        val escala by animateFloatAsState(
                            if (seleccionada == i) 1.25f else 1f,
                            spring(Spring.DampingRatioMediumBouncy), label = "escalaIcono"
                        )
                        NavigationBarItem(
                            selected = seleccionada == i,
                            onClick = { seleccionada = i },
                            icon = { Icon(icono, nombre, Modifier.scale(escala)) },
                            label = { Text(nombre, fontSize = 11.sp) }
                        )
                    }
                }
            }
        ) { padding ->
            // Transición entre pestañas: deslizamiento horizontal + fundido
            AnimatedContent(
                targetState = seleccionada,
                modifier = Modifier.padding(padding),
                transitionSpec = {
                    val dir = if (targetState > initialState) 1 else -1
                    (slideInHorizontally(tween(380)) { it * dir } + fadeIn(tween(380))) togetherWith
                            (slideOutHorizontally(tween(380)) { -it * dir } + fadeOut(tween(200)))
                },
                label = "pestanas"
            ) { indice ->
                when (pestanas[indice].first) {
                    "Explorar" -> ExplorarScreen(navController, appState)
                    "Cerca" -> CercaScreen(navController, appState)
                    "Beneficios" -> BeneficiosScreen(appState)
                    "Publicar" -> PublicarScreen(appState)
                    else -> PerfilScreen(navController, appState)
                }
            }
        }
    }
}
