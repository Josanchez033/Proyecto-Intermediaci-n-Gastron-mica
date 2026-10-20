package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.components.EncabezadoDegradado
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.theme.AzulPrimario

// Perfil secundario (Vendedor/Oferente): publica promociones para
// captar clientes en horarios o temporadas de baja demanda.
@Composable
fun PublicarScreen(appState: AppState) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var promocion by remember { mutableStateOf("") }
    var descuento by remember { mutableStateOf("20") }
    var horario by remember { mutableStateOf("Lun–Jue 15:00–19:00") }
    var error by remember { mutableStateOf("") }
    var publicado by remember { mutableStateOf(false) }

    Column(Modifier.verticalScroll(rememberScrollState())) {
        EncabezadoDegradado("Publicar promoción", "Llena tus horas de baja demanda")
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre del local") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(categoria, { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(promocion, { promocion = it }, label = { Text("Promoción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(descuento, { descuento = it.filter(Char::isDigit) }, label = { Text("% de descuento") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(horario, { horario = it }, label = { Text("Horario de baja demanda") }, modifier = Modifier.fillMaxWidth())
            if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
            PressButton("Publicar", Modifier.fillMaxWidth()) {
                if (nombre.isBlank() || promocion.isBlank()) {
                    error = "Nombre y promoción son obligatorios"
                    publicado = false
                } else {
                    error = ""
                    appState.publicarLocal(nombre.trim(), categoria.ifBlank { "Otros" }, promocion.trim(),
                        descuento.toIntOrNull() ?: 10, horario)
                    publicado = true
                    nombre = ""; promocion = ""
                }
            }
            AnimatedVisibility(publicado) {
                Text("✅ Promoción publicada. Ya aparece en Explorar.", color = AzulPrimario, fontWeight = FontWeight.Bold)
            }
        }
    }
}
