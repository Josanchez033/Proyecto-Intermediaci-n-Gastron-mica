package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.ResultadoLogin
import cl.dsy1105.cocinacapital.ui.components.AvisoBanner
import cl.dsy1105.cocinacapital.ui.components.CampoTexto
import cl.dsy1105.cocinacapital.ui.components.FotoLocal
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.theme.Aqua50
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.PetroleoProfundo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.utils.Campo
import cl.dsy1105.cocinacapital.utils.esEmailValido
import cl.dsy1105.cocinacapital.utils.validarLogin

// Acceso: una sola pantalla con pestañas "Iniciar sesión" y "Crear cuenta".
@Composable
fun LoginScreen(navController: NavController, appState: AppState, tabInicial: Int = 0) {
    var tab by rememberSaveable { mutableIntStateOf(tabInicial) }
    var cuentaCreada by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(Aqua50)) {
        // Foto de cabecera con velo petróleo
        Box(Modifier.fillMaxWidth().height(330.dp)) {
            FotoLocal("foto_mesa", null, Modifier.fillMaxSize())
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(PetroleoProfundo.copy(alpha = 0.55f), PetroleoProfundo.copy(alpha = 0.92f)))
                )
            )
            Column(
                Modifier.align(Alignment.Center).statusBarsPadding().offset(y = (-20).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(64.dp).clip(RoundedCornerShape(20.dp)).background(Color.White),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Filled.Restaurant, null, tint = Petroleo, modifier = Modifier.size(34.dp)) }
                Spacer(Modifier.height(12.dp))
                Text("Cocina Capital", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("Descubre, ahorra y saborea la región", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.88f))
            }
        }

        Column(
            Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).navigationBarsPadding()
                .padding(top = 250.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Surface(shape = RoundedCornerShape(28.dp), color = Color.White, shadowElevation = 6.dp) {
                Column {
                    SecondaryTabRow(
                        selectedTabIndex = tab,
                        containerColor = Color.White,
                        contentColor = Petroleo,
                        indicator = {
                            TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tab), height = 3.dp, color = Petroleo)
                        },
                        modifier = Modifier.clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    ) {
                        listOf("Iniciar sesión", "Crear cuenta").forEachIndexed { i, t ->
                            Tab(
                                selected = tab == i, onClick = { tab = i },
                                text = { Text(t, style = MaterialTheme.typography.labelLarge) },
                                selectedContentColor = Petroleo, unselectedContentColor = TintaSuave
                            )
                        }
                    }
                    AnimatedContent(
                        tab, transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                        label = "formAcceso"
                    ) { t ->
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            if (t == 0) {
                                if (cuentaCreada) AvisoBanner(Icons.Filled.Email, "Cuenta creada", "Ingresa con tu correo y contraseña.")
                                FormLogin(navController, appState)
                            } else {
                                FormRegistro(appState) { cuentaCreada = true; tab = 0 }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Prototipo con datos ficticios · Ley 19.628 y 21.719",
                style = MaterialTheme.typography.bodySmall, color = TintaSuave,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun FormLogin(navController: NavController, appState: AppState) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    // Validación en tiempo real: cada campo muestra su error en cuanto el usuario escribe en él.
    var tocados by rememberSaveable { mutableStateOf(setOf<String>()) }
    var intento by rememberSaveable { mutableStateOf(false) }
    var errorGeneral by remember { mutableStateOf<String?>(null) }
    var ingresando by remember { mutableStateOf(false) }
    val alcance = rememberCoroutineScope()
    val errores = validarLogin(email, password)
    fun error(campo: String) = if (intento || campo in tocados) errores[campo] else null

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column {
            Text("Bienvenido de nuevo", style = MaterialTheme.typography.titleLarge, color = Tinta)
            Text("Ingresa para ver las promociones pensadas para ti.", style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
        }
        CampoTexto(email, { email = it; tocados = tocados + Campo.EMAIL; errorGeneral = null }, "Correo electrónico", Icons.Filled.Email,
            error(Campo.EMAIL), teclado = KeyboardType.Email, valido = esEmailValido(email))
        CampoTexto(password, { password = it; tocados = tocados + Campo.PASSWORD; errorGeneral = null }, "Contraseña", Icons.Filled.Lock,
            error(Campo.PASSWORD) ?: errorGeneral, esPassword = true)
        TextButton(
            onClick = { navController.navigate("recuperar") },
            modifier = Modifier.align(Alignment.End)
        ) { Text("¿Olvidaste tu contraseña?") }
        PressButton(if (ingresando) "Ingresando…" else "Ingresar", Modifier.fillMaxWidth(), habilitado = !ingresando) {
            intento = true
            errorGeneral = null
            if (errores.isEmpty()) alcance.launch {
                ingresando = true
                val resultado = appState.login(email.trim(), password)
                ingresando = false
                when (resultado) {
                    ResultadoLogin.OK -> {
                        // El Admin tiene su propio panel; compradores y locales van a la app.
                        // Con una contraseña temporal del Admin, primero se crea una propia.
                        val destino = when {
                            appState.esAdmin -> "admin"
                            appState.usuarioActual?.claveTemporal == true -> "nueva-clave"
                            else -> "principal"
                        }
                        navController.navigate(destino) { popUpTo("login") { inclusive = true } }
                    }
                    ResultadoLogin.DESACTIVADO -> errorGeneral = "Esta cuenta de local está desactivada. Contacta a Cocina Capital."
                    ResultadoLogin.INCORRECTO -> errorGeneral = "Correo y/o contraseña incorrectos"
                }
            }
        }
    }
}
