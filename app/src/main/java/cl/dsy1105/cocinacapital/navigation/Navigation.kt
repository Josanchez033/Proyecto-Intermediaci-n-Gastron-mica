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
import cl.dsy1105.cocinacapital.ui.views.DetalleScreen
import cl.dsy1105.cocinacapital.ui.views.LoginScreen
import cl.dsy1105.cocinacapital.ui.views.PrincipalScreen
import cl.dsy1105.cocinacapital.ui.views.RegistroScreen

@Composable
fun AppNavigation(navController: NavHostController, appState: AppState) {
    NavHost(
        navController = navController, startDestination = "login",
        // Transiciones animadas entre pantallas
        enterTransition = { slideInHorizontally(tween(400)) { it } + fadeIn(tween(400)) },
        exitTransition = { slideOutHorizontally(tween(400)) { -it / 3 } + fadeOut(tween(400)) },
        popEnterTransition = { slideInHorizontally(tween(400)) { -it / 3 } + fadeIn(tween(400)) },
        popExitTransition = { slideOutHorizontally(tween(400)) { it } + fadeOut(tween(400)) }
    ) {
        composable("login") { LoginScreen(navController, appState) }
        composable("registro") { RegistroScreen(navController, appState) }
        composable("principal") { PrincipalScreen(navController, appState) }
        composable(
            "detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entrada ->
            DetalleScreen(navController, appState, entrada.arguments?.getLong("id") ?: 0L)
        }
    }
}
