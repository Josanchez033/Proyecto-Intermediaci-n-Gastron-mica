package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.ui.components.Aparecer
import cl.dsy1105.cocinacapital.ui.components.EncabezadoDegradado
import cl.dsy1105.cocinacapital.ui.theme.AzulPrimario
import cl.dsy1105.cocinacapital.ui.theme.AzulProfundo

@Composable
fun BeneficiosScreen(appState: AppState) {
    val canjes = appState.obtenerCanjes()
    Column {
        EncabezadoDegradado("Mis beneficios", "${canjes.size} canjeados")
        if (canjes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no has canjeado beneficios 🎟️", color = AzulProfundo)
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(canjes) { i, (canje, local) ->
                Aparecer(i * 70) {
                    Card(
                        Modifier.fillMaxWidth(), RoundedCornerShape(20.dp),
                        CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("${local.emoji} ${local.nombre}", fontWeight = FontWeight.Bold, color = AzulProfundo)
                            Text(local.promocion)
                            Text("Código: CC-${local.id}${canje.fecha % 10000}",
                                color = AzulPrimario, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
