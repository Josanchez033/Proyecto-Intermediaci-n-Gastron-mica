package cl.dsy1105.cocinacapital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.room.AppDataBase
import cl.dsy1105.cocinacapital.navigation.AppNavigation
import cl.dsy1105.cocinacapital.ui.theme.CocinaCapitalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // necesito hacer la carga inicial de datos
        val db = AppDataBase.getInstance(applicationContext)
        val appState = AppState(db)

        appState.cargarDatos() // carga inicial

        setContent {
            CocinaCapitalTheme {
                MyApp(appState)
            }
        }
    }
}

@Composable
fun MyApp(appState: AppState) {
    val navController = rememberNavController()
    AppNavigation(navController, appState)
}
