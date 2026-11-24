package cl.dsy1105.cocinacapital.data

import cl.dsy1105.cocinacapital.data.remote.ClimaActual
import cl.dsy1105.cocinacapital.data.remote.ClimaDiario
import cl.dsy1105.cocinacapital.data.remote.ClimaRespuesta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ClimaTest {

    private fun clima(temp: Int = 20, cielo: Cielo = Cielo.DESPEJADO, dia: Boolean = true, lluvia: Int? = 0) =
        Clima(temp, cielo, dia, temp + 2, temp - 6, lluvia)

    @Test
    fun codigosWmo() {
        assertEquals(Cielo.DESPEJADO, Clima.cieloDe(0))
        assertEquals(Cielo.NUBLADO, Clima.cieloDe(2))
        assertEquals(Cielo.NIEBLA, Clima.cieloDe(45))
        assertEquals(Cielo.LLOVIZNA, Clima.cieloDe(53))
        assertEquals(Cielo.LLUVIA, Clima.cieloDe(63))
        assertEquals(Cielo.LLUVIA, Clima.cieloDe(81))
        assertEquals(Cielo.NIEVE, Clima.cieloDe(73))
        assertEquals(Cielo.TORMENTA, Clima.cieloDe(95))
    }

    @Test
    fun convierteLaRespuestaDeOpenMeteo() {
        val r = ClimaRespuesta(ClimaActual(21.4, 0, 1), ClimaDiario(listOf(23.6), listOf(12.9), listOf(10)))
        val c = Clima.desde(r)!!
        assertEquals(21, c.temperatura)
        assertEquals(24, c.maxima)
        assertEquals(13, c.minima)
        assertEquals(10, c.probLluvia)
        assertEquals("Despejado", c.descripcion)
    }

    @Test
    fun sinClimaActual_devuelveNull() {
        assertNull(Clima.desde(ClimaRespuesta(null, null)))
    }

    @Test
    fun lluviaOFrio_sugiereAdentro() {
        assertTrue(clima(cielo = Cielo.LLUVIA).mejorAdentro)
        assertTrue(clima(temp = 8).mejorAdentro)
        assertTrue(clima(lluvia = 70).mejorAdentro)
        assertFalse(clima().mejorAdentro)
        assertTrue(clima(cielo = Cielo.LLUVIA).sugerencia.contains("techo"))
    }

    @Test
    fun calorODespejado_sugiereTerraza() {
        assertTrue(clima(temp = 27).sugerencia.contains("terraza"))
        assertTrue(clima(temp = 19).sugerencia.contains("terraza"))
        assertEquals("Noche despejada", clima(dia = false).descripcion)
    }

    @Test
    fun todasLasDescripciones() {
        val esperadas = mapOf(
            Cielo.NUBLADO to "Nublado", Cielo.NIEBLA to "Neblina", Cielo.LLOVIZNA to "Llovizna",
            Cielo.LLUVIA to "Lluvia", Cielo.TORMENTA to "Tormenta", Cielo.NIEVE to "Nieve"
        )
        esperadas.forEach { (cielo, texto) -> assertEquals(texto, clima(cielo = cielo).descripcion) }
        assertEquals(Cielo.NUBLADO, Clima.cieloDe(999)) // código desconocido
    }

    @Test
    fun todasLasSugerencias() {
        assertTrue(clima(cielo = Cielo.TORMENTA).sugerencia.contains("techo"))
        assertTrue(clima(cielo = Cielo.NUBLADO, lluvia = 80).sugerencia.contains("adentro"))
        assertTrue(clima(temp = 9, cielo = Cielo.NUBLADO).sugerencia.contains("cazuela"))
        assertEquals("Buena noche para salir a comer.", clima(dia = false).sugerencia)
        assertEquals("Buen día para salir a comer.", clima(cielo = Cielo.NUBLADO).sugerencia)
        assertTrue(clima(cielo = Cielo.NIEVE).mejorAdentro)
        assertFalse(clima(cielo = Cielo.NUBLADO, lluvia = null).mejorAdentro)
    }

    @Test
    fun respuestaSinPronosticoDiario() {
        val c = Clima.desde(ClimaRespuesta(ClimaActual(8.6, 61, 0), null))!!
        assertEquals(9, c.temperatura)
        assertEquals(Cielo.LLUVIA, c.cielo)
        assertNull(c.maxima)
        assertNull(c.probLluvia)
        assertFalse(c.esDia)
    }
}
