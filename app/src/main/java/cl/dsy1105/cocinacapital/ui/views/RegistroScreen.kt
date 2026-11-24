package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.components.CampoTexto
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.PetroleoProfundo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.utils.Campo
import cl.dsy1105.cocinacapital.utils.validarRegistro

// Formulario de registro (pestaña "Crear cuenta" de la pantalla de acceso).
@Composable
fun FormRegistro(appState: AppState, onCreada: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
    var tocados by rememberSaveable { mutableStateOf(setOf<String>()) }
    var intento by rememberSaveable { mutableStateOf(false) }
    var correoTomado by remember { mutableStateOf(false) }
    var creando by remember { mutableStateOf(false) }
    val alcance = rememberCoroutineScope()
    val errores = validarRegistro(email, password, confirmar) +
            if (correoTomado || appState.existeCorreo(email)) mapOf(Campo.EMAIL to "Ya existe una cuenta con ese correo") else emptyMap()
    fun error(campo: String) = if (intento || campo in tocados) errores[campo] else null

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column {
            Text("Crea tu cuenta", style = MaterialTheme.typography.titleLarge, color = Tinta)
            Text("Usa un correo de prueba: el prototipo trabaja con datos ficticios.", style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
        }
        CampoTexto(email, { email = it; correoTomado = false; tocados = tocados + Campo.EMAIL }, "Correo electrónico", Icons.Filled.Email,
            error(Campo.EMAIL), teclado = KeyboardType.Email, valido = Campo.EMAIL !in errores)
        CampoTexto(password, { password = it; tocados = tocados + Campo.PASSWORD }, "Contraseña", Icons.Filled.Lock,
            error(Campo.PASSWORD), esPassword = true, valido = Campo.PASSWORD !in errores,
            ayuda = if (Campo.PASSWORD !in errores) "Contraseña válida" else "Mínimo 6 caracteres, con al menos un número")
        CampoTexto(confirmar, { confirmar = it; tocados = tocados + Campo.CONFIRMAR }, "Repite la contraseña", Icons.Filled.LockReset,
            error(Campo.CONFIRMAR), esPassword = true, valido = Campo.CONFIRMAR !in errores,
            ayuda = if (Campo.CONFIRMAR !in errores) "Las contraseñas coinciden" else null)

        // Las cuentas de restaurante no se crean aquí: las crea el Admin de Cocina Capital
        // después de verificar el negocio (evita perfiles falsos y estafas).
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Aqua100).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Storefront, null, tint = PetroleoProfundo, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "¿Tienes un restaurante? Cocina Capital verifica cada local y te entrega tu cuenta.",
                style = MaterialTheme.typography.bodySmall, color = PetroleoProfundo
            )
        }
        PressButton(if (creando) "Creando cuenta…" else "Crear cuenta", Modifier.fillMaxWidth(), habilitado = !creando) {
            intento = true
            if (errores.isEmpty()) alcance.launch {
                creando = true
                val creada = appState.registrarComprador(email.trim(), password)
                creando = false
                if (creada) onCreada() else correoTomado = true
            }
        }
    }
}
