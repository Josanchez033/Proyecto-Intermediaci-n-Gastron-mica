package cl.dsy1105.cocinacapital.viewmodel

import cl.dsy1105.cocinacapital.data.Cielo
import cl.dsy1105.cocinacapital.data.remote.ClimaActual
import cl.dsy1105.cocinacapital.data.remote.ClimaApi
import cl.dsy1105.cocinacapital.data.remote.ClimaDiario
import cl.dsy1105.cocinacapital.data.remote.ClimaRespuesta
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/** ViewModel de la API externa (Open-Meteo), con la API simulada por MockK. */
@OptIn(ExperimentalCoroutinesApi::class)
class ClimaViewModelTest {

    private val api = mockk<ClimaApi>()
    private lateinit var vm: ClimaViewModel

    private val soleado = ClimaRespuesta(ClimaActual(21.4, 0, 1), ClimaDiario(listOf(23.0), listOf(13.0), listOf(0)))

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        vm = ClimaViewModel(api)
    }

    @After
    fun limpiar() = Dispatchers.resetMain()

    @Test
    fun empiezaCargando() {
        assertEquals(EstadoClima.Cargando, vm.estado.value)
    }

    @Test
    fun conRespuesta_muestraElClima() {
        coEvery { api.pronostico(any(), any(), any(), any(), any(), any()) } returns soleado
        vm.cargar(-33.0458, -71.6197)
        val clima = (vm.estado.value as EstadoClima.Listo).clima
        assertEquals(21, clima.temperatura)
        assertEquals(Cielo.DESPEJADO, clima.cielo)
        assertEquals(23, clima.maxima)
    }

    @Test
    fun enviaLaUbicacionRedondeada_aUnKilometro() {
        coEvery { api.pronostico(any(), any(), any(), any(), any(), any()) } returns soleado
        vm.cargar(-33.045812, -71.619734)
        coVerify { api.pronostico(-33.05, -71.62, any(), any(), any(), any()) }
        assertEquals(-33.05, ClimaViewModel.redondear(-33.045812), 0.0)
    }

    @Test
    fun mismaUbicacion_noVuelveAConsultar() {
        coEvery { api.pronostico(any(), any(), any(), any(), any(), any()) } returns soleado
        vm.cargar(-33.0458, -71.6197)
        vm.cargar(-33.0459, -71.6198) // mismo punto redondeado
        coVerify(exactly = 1) { api.pronostico(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun sinInternet_seOculta() {
        coEvery { api.pronostico(any(), any(), any(), any(), any(), any()) } throws IOException("sin red")
        vm.cargar(-33.0458, -71.6197)
        assertEquals(EstadoClima.NoDisponible, vm.estado.value)
    }

    @Test
    fun respuestaSinClimaActual_seOculta() {
        coEvery { api.pronostico(any(), any(), any(), any(), any(), any()) } returns ClimaRespuesta(null, null)
        vm.cargar(-33.0458, -71.6197)
        assertTrue(vm.estado.value is EstadoClima.NoDisponible)
    }
}
