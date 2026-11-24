package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.theme.Aqua200
import cl.dsy1105.cocinacapital.ui.theme.FondoApp
import cl.dsy1105.cocinacapital.ui.theme.PetroleoProfundo
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave

private data class Pestana(val nombre: String, val activo: ImageVector, val inactivo: ImageVector)

// Contenedor principal: barra de navegación inferior Material.
// Solo para compradores; el Local usa LocalPrincipalScreen.
@Composable
fun PrincipalScreen(navController: NavController, appState: AppState) {
    // El Local tiene su propia app: no ve el inicio ni los demás locales.
    if (appState.esVendedor) {
        LocalPrincipalScreen(navController, appState)
        return
    }
    val pestanas = buildList {
        add(Pestana("Inicio", Icons.Filled.Home, Icons.Outlined.Home))
        add(Pestana("Cerca", Icons.Filled.NearMe, Icons.Outlined.NearMe))
        add(Pestana("Vouchers", Icons.Filled.ConfirmationNumber, Icons.Outlined.ConfirmationNumber))
        add(Pestana("Perfil", Icons.Filled.Person, Icons.Outlined.Person))
    }
    var seleccionada by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }

    // Aviso transitorio si no se pudo sincronizar con el backend (RNF-26).
    val snackbar = remember { SnackbarHostState() }
    val aviso = appState.avisoSincronizacion
    LaunchedEffect(aviso) {
        if (aviso != null) {
            snackbar.showSnackbar(aviso, withDismissAction = true)
            appState.avisoSincronizacion = null
        }
    }

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbar) },
            bottomBar = {
                NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                    pestanas.forEachIndexed { i, p ->
                        NavigationBarItem(
                            selected = seleccionada == i,
                            onClick = { seleccionada = i },
                            icon = { Icon(if (seleccionada == i) p.activo else p.inactivo, null) },
                            label = { Text(p.nombre) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Aqua200,
                                selectedIconColor = PetroleoProfundo, selectedTextColor = PetroleoProfundo,
                                unselectedIconColor = TintaSuave, unselectedTextColor = TintaSuave
                            )
                        )
                    }
                }
            }
        ) { padding ->
            // Transición entre pestañas: fundido con leve escala (fade-through de Material)
            AnimatedContent(
                targetState = seleccionada.coerceAtMost(pestanas.lastIndex),
                modifier = Modifier.padding(padding),
                transitionSpec = {
                    (fadeIn(tween(220, 90)) + scaleIn(tween(220, 90), initialScale = 0.96f)) togetherWith fadeOut(tween(90))
                },
                label = "pestanas"
            ) { indice ->
                when ((pestanas.getOrNull(indice) ?: pestanas.last()).nombre) {
                    "Inicio" -> ExplorarScreen(navController, appState)
                    "Cerca" -> CercaScreen(navController, appState)
                    "Vouchers" -> BeneficiosScreen(navController, appState) { seleccionada = 0 }
                    else -> PerfilScreen(navController, appState)
                }
            }
        }
    }
}

