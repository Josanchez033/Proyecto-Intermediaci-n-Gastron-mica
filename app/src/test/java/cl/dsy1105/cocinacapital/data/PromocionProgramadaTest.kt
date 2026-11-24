package cl.dsy1105.cocinacapital.data

import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.room.Oferta
import cl.dsy1105.cocinacapital.utils.diaSemana
import cl.dsy1105.cocinacapital.utils.inicioDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PromocionProgramadaTest {

    private val viernes = (0..6).map { inicioDelDia(it) }.first { diaSemana(it) == 5 }

    // 2x1 todos los viernes de 19 a 23, durante un mes desde el próximo viernes.
    private val promo = LocalEntity(
        1, "Local", "Italiana", "Valparaíso", "Calle 1", "2x1 pizzas", "", "", 50, 10000, "5", 19, 23, 8, 4.5,
        -33.04, -71.62, "foto_pizza", oferta = Oferta.DOS_POR_UNO, desde = viernes, hasta = viernes + 27L * 86_400_000L
    )

    private fun buscar(fecha: Long) = Recomendador.buscarPropuestas(
        listOf(promo), CriteriosBusqueda(5, 20, 2, 20.0, TODAS, fecha), emptyMap(), -33.04, -71.62, matchMinimo = 0
    )

    @Test
    fun apareceDentroDeSuPeriodo() {
        assertEquals(1, buscar(viernes).size)
        assertEquals(1, buscar(viernes + 7L * 86_400_000L).size)
    }

    @Test
    fun noApareceAntesNiDespues() {
        assertTrue(buscar(viernes - 7L * 86_400_000L).isEmpty())
        assertTrue(buscar(viernes + 35L * 86_400_000L).isEmpty())
    }

    @Test
    fun sinFechas_vigenteSiempre() {
        val siempre = promo.copy(desde = 0, hasta = 0)
        assertTrue(Recomendador.vigenteEl(siempre, inicioDelDia(400)))
        assertFalse(terminada(siempre))
    }

    @Test
    fun cuatroViernesEnElMes() {
        assertEquals(4, vecesEnPeriodo("5", promo.desde, promo.hasta))
    }

    @Test
    fun terminadaYProgramada() {
        assertTrue(terminada(promo.copy(desde = 0, hasta = inicioDelDia(-1))))
        assertTrue(programada(promo.copy(desde = inicioDelDia(3))))
        assertFalse(programada(promo.copy(desde = 0)))
    }

    @Test
    fun etiquetas() {
        assertEquals("2x1", promo.etiquetaOferta)
        assertEquals("3x2", promo.copy(oferta = Oferta.TRES_POR_DOS).etiquetaOferta)
        assertEquals("-30%", promo.copy(oferta = Oferta.PORCENTAJE, descuento = 30).etiquetaOferta)
        val combo = promo.copy(oferta = Oferta.PERSONALIZADA, etiqueta = "Combo", precioNormal = 14000, precioOferta = 9900)
        assertEquals("Combo", combo.etiquetaOferta)
        assertEquals(9900, combo.precioPromo) // el precio que fijó el Local, exacto
        assertEquals("todos los viernes", textoDiasLargo("5"))
        assertEquals("todos los sábados", textoDiasLargo("6"))
    }
}
