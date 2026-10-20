package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.components.Aparecer
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.theme.AzulProfundo
import cl.dsy1105.cocinacapital.ui.theme.Celeste
import cl.dsy1105.cocinacapital.ui.theme.DegradadoHero

@Composable
fun LoginScreen(navController: NavController,
                appState: AppState) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    // Animación infinita: los círculos difuminados "respiran"
    val pulso by rememberInfiniteTransition(label = "pulso").animateFloat(
        initialValue = 1f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "escala"
    )

    Box(Modifier.fillMaxSize().background(DegradadoHero)) {
        Box(Modifier.offset((-60).dp, 80.dp).size(220.dp).scale(pulso)
            .background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)), CircleShape))
        Box(Modifier.align(Alignment.BottomEnd).offset(50.dp, 40.dp).size(260.dp).scale(pulso)
            .background(Brush.radialGradient(listOf(Celeste.copy(alpha = 0.6f), Color.Transparent)), CircleShape))

        Scaffold(containerColor = Color.Transparent) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Aparecer {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🍽️", fontSize = 64.sp, modifier = Modifier.scale(pulso))
                        Text("Cocina Capital", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Descubre, ahorra y saborea la región", color = Color.White.copy(alpha = 0.85f))
                    }
                }
                Spacer(Modifier.height(24.dp))
                Aparecer(200) {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(10.dp)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Iniciar Sesión", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AzulProfundo)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = usuario,
                                onValueChange = { usuario = it },
                                label = { Text("Usuario (email)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp)) // Para dibujar espacios entre un control y otro
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Contraseña") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(16.dp))
                            // verifico si hay error
                            if (error.isNotEmpty()) {
                                Text(error, color = MaterialTheme.colorScheme.error)
                                Spacer(Modifier.height(8.dp))
                            }
                            PressButton("Iniciar Sesión", Modifier.fillMaxWidth()) {
                                // verificar las reglas de negocio
                                if (usuario.isBlank() || password.isBlank()) {
                                    error = "Debe ingresar Usuario y Contraseña"
                                } else if (password.length < 3) {
                                    error = "La contraseña debe tener al menos 3 caracteres"
                                } else if (appState.login(usuario.trim(), password)) {
                                    error = ""
                                    // Ir a la pantalla principal y sacar Login de la pila,
                                    // para que "atrás" no vuelva al Login
                                    navController.navigate("principal") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    error = "Usuario y/o Contraseña incorrectos"
                                }
                            }
                            TextButton(onClick = { navController.navigate("registro") }) {
                                Text("¿No tienes Cuenta?... Regístrate Aquí!!")
                            }
                        }
                    }
                }
            }
        }
    }
}
