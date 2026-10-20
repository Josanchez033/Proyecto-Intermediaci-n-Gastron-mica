package cl.dsy1105.cocinacapital.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Paleta blanco / celeste / azul
val AzulProfundo = Color(0xFF0D47A1)
val AzulPrimario = Color(0xFF1976D2)
val Celeste = Color(0xFF4FC3F7)
val CelesteClaro = Color(0xFFB3E5FC)
val Hielo = Color(0xFFE3F2FD)

// Degradados reutilizables en toda la app
val DegradadoHero = Brush.linearGradient(listOf(AzulProfundo, AzulPrimario, Celeste))
val DegradadoFondo = Brush.verticalGradient(listOf(Color.White, Hielo, CelesteClaro))
