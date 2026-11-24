package cl.dsy1105.cocinacapital.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FechasTest {

    @Test
    fun hoy_soloDesdeLaHoraActual() {
        assertEquals(18..22, horasDisponibles(inicioDelDia(0), horaActual = 18))
    }

    @Test
    fun hoy_temprano_partenALas8() {
        assertEquals(8..22, horasDisponibles(inicioDelDia(0), horaActual = 3))
    }

    @Test
    fun hoy_despuesDeLas22_noQuedanHoras() {
        assertTrue(horasDisponibles(inicioDelDia(0), horaActual = 23).isEmpty())
    }

    @Test
    fun otroDia_todasLasHoras() {
        assertEquals(8..22, horasDisponibles(inicioDelDia(1), horaActual = 20))
        assertEquals(8..22, horasDisponibles(inicioDelDia(9), horaActual = 20))
    }

    @Test
    fun diasDesdeHoy_yDiaSemana_soncoherentes() {
        for (offset in 0..20) {
            val fecha = inicioDelDia(offset)
            assertEquals(offset, diasDesdeHoy(fecha))
            assertEquals(diaSemana(fecha), diaSemana(inicioDelDia(offset + 7)))
        }
    }

    @Test
    fun proximaFecha_nuncaEsPasada() {
        for (dia in 1..7) {
            val fecha = proximaFecha(dia)
            assertEquals(dia, diaSemana(fecha))
            assertTrue(diasDesdeHoy(fecha) in 0..6)
        }
    }

    @Test
    fun calendarioUtc_idaYVuelta() {
        for (offset in 0..60) {
            val fecha = inicioDelDia(offset)
            assertEquals(fecha, desdeUtc(aUtc(fecha)))
        }
    }

    @Test
    fun textosDeFecha() {
        assertEquals("Hoy", textoFechaCorta(inicioDelDia(0)))
        assertEquals("Mañana", textoFechaCorta(inicioDelDia(1)))
        assertTrue(textoFechaCorta(inicioDelDia(5)).matches(Regex("""\p{L}{3} \d{1,2} \p{L}{3}""")))
        assertTrue(textoFechaLarga(inicioDelDia(0)).startsWith("Hoy "))
        assertTrue(textoFechaLarga(inicioDelDia(1)).startsWith("Mañana "))
        assertTrue(textoFechaLarga(inicioDelDia(5)).contains(" de "))
    }

    @Test
    fun sumarDiasYMeses() {
        val hoy = inicioDelDia(0)
        assertEquals(inicioDelDia(7), sumarDias(hoy, 7))
        assertTrue(finDeMeses(hoy, 1) in sumarDias(hoy, 26)..sumarDias(hoy, 31))
    }
}
