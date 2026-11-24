package cl.dsy1105.cocinacapital.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.views.AdminScreen
import cl.dsy1105.cocinacapital.ui.views.BusquedaScreen
import cl.dsy1105.cocinacapital.ui.views.CrearLocalScreen
import cl.dsy1105.cocinacapital.ui.views.DetalleScreen
import cl.dsy1105.cocinacapital.ui.views.EditarRestauranteScreen
import cl.dsy1105.cocinacapital.ui.views.RestauranteScreen
import cl.dsy1105.cocinacapital.ui.views.LoginScreen
import cl.dsy1105.cocinacapital.ui.views.MiCuentaScreen
import cl.dsy1105.cocinacapital.ui.views.NuevaClaveScreen
import cl.dsy1105.cocinacapital.ui.views.RecuperarClaveScreen
import cl.dsy1105.cocinacapital.ui.views.PrincipalScreen
import cl.dsy1105.cocinacapital.ui.views.PropuestasScreen
import cl.dsy1105.cocinacapital.ui.views.PublicarScreen
import cl.dsy1105.cocinacapital.ui.views.VoucherScreen

@Composable
fun AppNavigation(navController: NavHostController, appState: AppState) {
    NavHost(
        navController = navController, startDestination = "login",
        // Transiciones entre pantallas: eje compartido horizontal de Material
        enterTransition = { slideInHorizontally(tween(320)) { it / 4 } + fadeIn(tween(320)) },
        exitTransition = { slideOutHorizontally(tween(320)) { -it / 6 } + fadeOut(tween(200)) },
        popEnterTransition = { slideInHorizontally(tween(320)) { -it / 6 } + fadeIn(tween(320)) },
        popExitTransition = { slideOutHorizontally(tween(320)) { it / 4 } + fadeOut(tween(200)) }
    ) {
        composable("login") { LoginScreen(navController, appState) }
        composable("registro") { LoginScreen(navController, appState, tabInicial = 1) }
        composable("recuperar") { RecuperarClaveScreen(navController, appState) }
        composable("nueva-clave") { NuevaClaveScreen(navController, appState) }
        composable("cuenta") { MiCuentaScreen(navController, appState) }
        composable("principal") { PrincipalScreen(navController, appState) }
        composable("admin") { AdminScreen(navController, appState) }
        composable("admin/nuevo") { CrearLocalScreen(navController, appState) }
        composable("busqueda") { BusquedaScreen(navController, appState) }
        composable("propuestas") { PropuestasScreen(navController, appState) }
        composable("detalle/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { e ->
            DetalleScreen(navController, appState, e.arguments?.getLong("id") ?: 0L)
        }
        composable("voucher/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { e ->
            VoucherScreen(navController, appState, e.arguments?.getLong("id") ?: 0L)
        }
        composable("restaurante/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { e ->
            RestauranteScreen(navController, appState, e.arguments?.getLong("id") ?: 0L)
        }
        composable("mi-restaurante/editar") { EditarRestauranteScreen(navController, appState) }
        composable("publicar/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { e ->
            PublicarScreen(navController, appState, e.arguments?.getLong("id") ?: 0L)
        }
    }
}
