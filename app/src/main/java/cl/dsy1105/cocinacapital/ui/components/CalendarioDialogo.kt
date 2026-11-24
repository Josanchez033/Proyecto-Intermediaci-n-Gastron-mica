package cl.dsy1105.cocinacapital.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.utils.aUtc
import cl.dsy1105.cocinacapital.utils.desdeUtc
import cl.dsy1105.cocinacapital.utils.inicioDelDia
import cl.dsy1105.cocinacapital.utils.textoFechaLarga
import java.util.Calendar
import java.util.Locale

/**
 * Calendario de Material en español de Chile (aunque el teléfono esté en otro idioma).
 * Solo deja elegir entre hoy + [primerDia] y hoy + [ultimoDia]: nunca fechas pasadas.
 * Lo usan "Arma tu salida" y la programación de promociones del Local.
 */
@Composable
fun CalendarioDialogo(
    titulo: String,
    fecha: Long,
    primerDia: Int,
    ultimoDia: Int,
    onCerrar: () -> Unit,
    onElegir: (Long) -> Unit
) {
    val config = LocalConfiguration.current
    val enEspanol = remember(config) { Configuration(config).apply { setLocale(Locale("es", "CL")) } }
    // El estado del calendario se crea dentro del idioma español para que meses y días salgan traducidos.
    CompositionLocalProvider(LocalConfiguration provides enEspanol) {
        Calendario(titulo, fecha, primerDia, ultimoDia, onCerrar, onElegir)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Calendario(
    titulo: String, fecha: Long, primerDia: Int, ultimoDia: Int, onCerrar: () -> Unit, onElegir: (Long) -> Unit
) {
    val minimo = aUtc(inicioDelDia(primerDia))
    val maximo = aUtc(inicioDelDia(ultimoDia))
    val anio = Calendar.getInstance().get(Calendar.YEAR)
    val estado = rememberDatePickerState(
        initialSelectedDateMillis = aUtc(fecha).coerceIn(minimo, maximo),
        yearRange = anio..anio + 2,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis in minimo..maximo
            override fun isSelectableYear(year: Int) = year in anio..anio + 2
        }
    )
    val colores = DatePickerDefaults.colors(
        containerColor = Color.White, selectedDayContainerColor = Petroleo, todayDateBorderColor = Petroleo,
        todayContentColor = Petroleo, selectedYearContainerColor = Petroleo
    )
    DatePickerDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            TextButton(enabled = estado.selectedDateMillis != null, onClick = {
                estado.selectedDateMillis?.let { onElegir(desdeUtc(it)) }
                onCerrar()
            }) { Text("Listo") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } },
        colors = colores
    ) {
        DatePicker(
            estado, colors = colores, showModeToggle = false,
            title = { Text(titulo, Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)) },
            headline = {
                Text(
                    estado.selectedDateMillis?.let { textoFechaLarga(desdeUtc(it)) } ?: "Elige un día",
                    Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        )
    }
}
