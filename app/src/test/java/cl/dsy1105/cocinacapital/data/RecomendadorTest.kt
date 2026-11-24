package cl.dsy1105.cocinacapital.data

import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.utils.inicioDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** El corazón de la búsqueda específica: condiciones duras, % de match y alternativas. */
class RecomendadorTest {

    private val lat = -33.0458
    private val lon = -71.6197

    private fun local(
        id: Long, categoria: String = "Italiana", dias: String = "1234567", desde: Int = 12, hasta: Int = 22,
        capacidad: Int = 6, descuento: Int = 30, latitud: Double = lat, longitud: Double = lon, vendedor: String? = null
    ) = LocalEntity(
        id, "Local $id", categoria, "Valparaíso", "Calle $id", "Promo $id", "", "", descuento, 10000, dias,
        desde, hasta, capacidad, 4.5, latitud, longitud, "foto_pasta", vendedorEmail = vendedor
    )

    private val jueves16 = CriteriosBusqueda(dia = 4, hora = 16, personas = 4, radioKm = 5.0, preferencia = "Italiana")

    @Test
    fun distancia_entreValparaisoYVina() {
        val km = Recomendador.distanciaKm(-33.0458, -71.6197, -33.0245, -71.5518)
        assertTrue(km in 5.0..8.0)
        assertEquals(0.0, Recomendador.distanciaKm(lat, lon, lat, lon), 0.0001)
    }

    @Test
    fun condicionesDuras_diaHoraCupoYRadio() {
        val locales = listOf(
            local(1),                                   // calza
            local(2, dias = "1"),                       // no abre el jueves
            local(3, desde = 18),                       // abre después
            local(4, capacidad = 2),                    // no hay cupo para 4
            local(5, latitud = -33.2, longitud = -71.9) // muy lejos
        )
        val ids = Recomendador.buscarPropuestas(locales, jueves16, emptyMap(), lat, lon).map { it.local.id }
        assertEquals(listOf(1L), ids)
    }

    @Test
    fun match_preferenciaYPerfilSubenElPorcentaje() {
        val italiana = local(1)
        val sushi = local(2, categoria = "Sushi")
        val propuestas = Recomendador.buscarPropuestas(listOf(sushi, italiana), jueves16, mapOf("Italiana" to 5), lat, lon, matchMinimo = 0)
        assertEquals(1L, propuestas.first().local.id)
        assertTrue(propuestas.first().match > propuestas.last().match)
        assertTrue(propuestas.first().razones.any { it.contains("lo que buscas") })
        assertTrue(propuestas.first().razones.any { it.contains("Tu perfil") })
        assertTrue(propuestas.first().razones.contains("Cupo para 4"))
    }

    @Test
    fun unaPropuestaPorRestaurante() {
        val a = local(1, vendedor = "trattoria@correo.cl", descuento = 20)
        val b = local(2, vendedor = "trattoria@correo.cl", descuento = 40)
        val propuestas = Recomendador.buscarPropuestas(listOf(a, b), jueves16, emptyMap(), lat, lon)
        assertEquals(1, propuestas.size)
        assertEquals(2L, propuestas.single().local.id) // queda la mejor
        assertEquals("trattoria@correo.cl", Recomendador.restaurante(a))
        assertEquals("catalogo:Local 9", Recomendador.restaurante(local(9)))
    }

    @Test
    fun alternativas_otraHoraMasLejosUOtraComida() {
        val tarde = local(1, desde = 19, hasta = 23)                       // 3 horas después
        val lejos = local(2, latitud = -33.0245, longitud = -71.5518)     // ~7 km
        val otra = local(3, categoria = "Carnes")
        val exacta = local(4)
        val alternativas = Recomendador.buscarAlternativas(listOf(tarde, lejos, otra, exacta), jueves16, emptyMap(), lat, lon)
        val porId = alternativas.associateBy { it.local.id }
        assertTrue(porId[1]!!.ajuste!!.contains("A las 19:00"))
        assertTrue(porId[2]!!.ajuste!!.contains("km"))
        assertTrue(porId[3]!!.ajuste!!.lowercase().contains("es carnes"))
        assertNull(porId[4]) // calza exacto: ya va en las propuestas
        assertTrue(alternativas.all { it.match in 1..99 })
    }

    @Test
    fun alternativas_respetanDiaYCupo() {
        val otroDia = local(1, dias = "6", categoria = "Carnes")
        val sinCupo = local(2, capacidad = 1, categoria = "Carnes")
        assertTrue(Recomendador.buscarAlternativas(listOf(otroDia, sinCupo), jueves16, emptyMap(), lat, lon).isEmpty())
    }

    @Test
    fun vigencia_programadaYTerminada() {
        val enUnaSemana = inicioDelDia(7)
        val programada = local(1).copy(desde = enUnaSemana)
        assertTrue(programada(programada))
        assertTrue(Recomendador.vigenteEl(programada, enUnaSemana))
        assertTrue(!Recomendador.vigenteEl(programada, inicioDelDia(0)))
        val vencida = local(2).copy(hasta = inicioDelDia(-2))
        assertTrue(terminada(vencida))
        assertTrue(textoVigencia(vencida)!!.startsWith("Terminó el"))
        assertTrue(textoVigencia(programada)!!.startsWith("Desde el"))
        assertTrue(textoVigencia(programada.copy(hasta = inicioDelDia(30)))!!.startsWith("Del "))
        assertTrue(textoVigencia(local(3).copy(hasta = inicioDelDia(10)))!!.startsWith("Hasta el"))
        assertNull(textoVigencia(local(4)))
    }

    @Test
    fun puntajeParaTi_premiaAfinidadCercaniaYDescuento() {
        val base = Recomendador.puntajeParaTi(0, 1.0, 20)
        assertTrue(Recomendador.puntajeParaTi(5, 1.0, 20) > base)
        assertTrue(Recomendador.puntajeParaTi(0, 0.2, 20) > base)
        assertTrue(Recomendador.puntajeParaTi(0, 1.0, 50) > base)
    }

    @Test
    fun textosDeDiasHorarioYPrecio() {
        assertEquals("Todos los días", textoDias("1234567"))
        assertEquals("Lun–Jue", textoDias("1234"))
        assertEquals("Lun, Mié, Vie", textoDias("135"))
        assertEquals("Sin días", textoDias(""))
        assertEquals("todos los viernes", textoDiasLargo("5"))
        assertEquals("todos los días", textoDiasLargo("1234567"))
        assertEquals("los días Lun–Jue", textoDiasLargo("1234"))
        assertEquals("Viernes", nombreDia(5))
        assertEquals("Dom", diaCorto(7))
        assertEquals("Lun–Jue · 15:00–19:00", textoHorario(local(1, dias = "1234", desde = 15, hasta = 19)))
        assertEquals("$11.900", pesos(11900))
        assertEquals("CC-090123", codigoCanje(109, 1_000_123))
        assertEquals(7000, local(1).precioPromo)
    }
}
