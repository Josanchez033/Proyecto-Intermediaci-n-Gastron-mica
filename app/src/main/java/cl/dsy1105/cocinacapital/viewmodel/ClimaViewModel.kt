package cl.dsy1105.cocinacapital.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.dsy1105.cocinacapital.data.Clima
import cl.dsy1105.cocinacapital.data.remote.ClimaApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Lo que muestra la tarjeta del clima en el inicio. */
sealed interface EstadoClima {
    data object Cargando : EstadoClima
    data class Listo(val clima: Clima) : EstadoClima
    /** Sin internet o la API no respondió: la tarjeta se oculta y el resto de la app sigue igual. */
    data object NoDisponible : EstadoClima
}

/**
 * Consume la API externa Open-Meteo. Está separado de AppState a propósito:
 * el clima no se guarda en Room ni pasa por los microservicios propios.
 */
class ClimaViewModel(private val api: ClimaApi = ClimaApi.instancia) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoClima>(EstadoClima.Cargando)
    val estado: StateFlow<EstadoClima> = _estado.asStateFlow()

    private var ultimaConsulta: Pair<Double, Double>? = null

    /**
     * Pide el clima para la ubicación del usuario. Las coordenadas se redondean a 2 decimales
     * (~1 km): basta para el pronóstico y no se envía la ubicación exacta a un tercero (Ley 21.719).
     */
    fun cargar(latitud: Double, longitud: Double) {
        val punto = redondear(latitud) to redondear(longitud)
        if (punto == ultimaConsulta && _estado.value is EstadoClima.Listo) return
        ultimaConsulta = punto
        viewModelScope.launch {
            _estado.value = try {
                Clima.desde(api.pronostico(punto.first, punto.second))
                    ?.let { EstadoClima.Listo(it) } ?: EstadoClima.NoDisponible
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (_: Exception) {
                EstadoClima.NoDisponible
            }
        }
    }

    companion object {
        fun redondear(valor: Double) = (valor * 100).roundToInt() / 100.0
    }
}
