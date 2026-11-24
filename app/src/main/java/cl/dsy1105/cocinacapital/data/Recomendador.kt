package cl.dsy1105.cocinacapital.data

import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.utils.diaSemana
import cl.dsy1105.cocinacapital.utils.inicioDelDia
import cl.dsy1105.cocinacapital.utils.mesCorto
import cl.dsy1105.cocinacapital.utils.numeroDia
import cl.dsy1105.cocinacapital.utils.proximaFecha
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

// Lógica de recomendación SIN dependencias de Android: se puede probar con JUnit.

const val TODAS = "Todas"

/** Lo que el consumidor pide en la búsqueda específica. */
data class CriteriosBusqueda(
    val dia: Int,            // 1 = lunes ... 7 = domingo
    val hora: Int,           // 0..23
    val personas: Int,
    val radioKm: Double,
    val preferencia: String, // categoría o "Todas"
    val fecha: Long? = null  // fecha exacta elegida (medianoche local); null = la próxima con ese día
)

/** Un local que calza con la búsqueda, con su % de match y el porqué. */
data class Propuesta(
    val local: LocalEntity,
    val match: Int,
    val distanciaKm: Double,
    val razones: List<String>,
    /** Si no calza exacto: qué cambió respecto de lo pedido (otra hora, más lejos, otra comida). */
    val ajuste: String? = null
)

object Recomendador {

    /** Distancia en km entre dos puntos (fórmula de Haversine). */
    fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return 2 * 6371.0 * asin(sqrt(a))
    }

    /** RN-04: puntaje "Para ti" = afinidad×2 − km×0,3 + descuento÷10. */
    fun puntajeParaTi(afinidad: Int, distanciaKm: Double, descuento: Int): Double =
        afinidad * 2.0 - distanciaKm * 0.3 + descuento / 10.0

    /** ¿La promoción del local está vigente ese día a esa hora? */
    fun abreEn(local: LocalEntity, dia: Int, hora: Int): Boolean =
        local.dias.contains(dia.toString()) && hora >= local.horaInicio && hora < local.horaFin

    /** ¿La promoción programada está vigente esa fecha? desde/hasta en 0 = sin límite. */
    fun vigenteEl(local: LocalEntity, fecha: Long): Boolean =
        (local.desde == 0L || fecha >= local.desde) && (local.hasta == 0L || fecha <= local.hasta)

    /** La fecha de la búsqueda: la elegida, o la próxima vez que cae ese día de la semana. */
    private fun fechaDe(criterios: CriteriosBusqueda) = criterios.fecha ?: proximaFecha(criterios.dia)

    /**
     * Búsqueda específica: filtra por día/hora, cupo y radio (condiciones duras),
     * calcula el % de match (preferencia elegida, perfil de consumo, cercanía y
     * descuento) y entrega las mejores propuestas (máximo 5, con match mínimo).
     */
    /** Puntaje 1..99 y razones de un local que ya cumple las condiciones duras. */
    private fun evaluar(
        local: LocalEntity, km: Double, criterios: CriteriosBusqueda, perfil: Map<String, Int>, maxAfinidad: Int
    ): Pair<Int, List<String>> {
        val razones = mutableListOf<String>()
        var puntos = 0.0
        val pideCategoria = criterios.preferencia != TODAS
        if (pideCategoria && local.categoria == criterios.preferencia) {
            puntos += 40; razones += "Es ${local.categoria.lowercase()}, lo que buscas"
        } else if (!pideCategoria) {
            puntos += 20
        }
        val afinidad = perfil[local.categoria] ?: 0
        if (afinidad > 0) {
            puntos += 25.0 * afinidad / maxAfinidad
            razones += "Tu perfil: te gusta ${local.categoria.lowercase()}"
        }
        puntos += 20.0 * (1 - km / criterios.radioKm).coerceIn(0.0, 1.0)
        razones += "A %.1f km".format(km).replace('.', ',')
        puntos += 15.0 * (local.descuento.coerceAtMost(50) / 50.0)
        razones += "-${local.descuento}% en hora de baja demanda"
        razones += "Cupo para ${criterios.personas}"
        return puntos.roundToInt().coerceIn(1, 99) to razones
    }

    /** Identifica al restaurante: su cuenta de Local o, en el catálogo base, su nombre. */
    fun restaurante(local: LocalEntity): String = local.vendedorEmail ?: "catalogo:${local.nombre}"

    private fun maxAfinidad(perfil: Map<String, Int>) = (perfil.values.maxOrNull() ?: 0).coerceAtLeast(1)

    /**
     * Búsqueda específica: filtra por día/hora, cupo y radio (condiciones duras),
     * calcula el % de match (preferencia elegida, perfil de consumo, cercanía y
     * descuento) y entrega las mejores propuestas (máximo 5, con match mínimo).
     */
    fun buscarPropuestas(
        locales: List<LocalEntity>,
        criterios: CriteriosBusqueda,
        perfil: Map<String, Int>,
        lat: Double,
        lon: Double,
        maxPropuestas: Int = 5,
        matchMinimo: Int = 35
    ): List<Propuesta> {
        val maxAf = maxAfinidad(perfil)
        val fecha = fechaDe(criterios)
        return locales.mapNotNull { local ->
            val km = distanciaKm(lat, lon, local.latitud, local.longitud)
            val disponible = abreEn(local, criterios.dia, criterios.hora) && vigenteEl(local, fecha) &&
                    local.capacidad >= criterios.personas &&
                    km <= criterios.radioKm
            if (!disponible) return@mapNotNull null
            val (match, razones) = evaluar(local, km, criterios, perfil, maxAf)
            Propuesta(local, match, km, razones)
        }.filter { it.match >= matchMinimo }
            .sortedWith(compareByDescending<Propuesta> { it.match }.thenBy { it.distanciaKm })
            .distinctBy { restaurante(it.local) } // propuestas de restaurantes distintos: la mejor promo de cada uno
            .take(maxPropuestas)
    }

    /**
     * Cuando ningún local calza exacto, propone los más parecidos el MISMO día:
     * hasta [horasHolgura] horas antes o después, hasta [radioMaxKm] km y de
     * cualquier comida. Cada alternativa dice qué cambió ("ajuste"), y pierde
     * puntos por cada cosa que no calza, para que el usuario decida informado.
     */
    fun buscarAlternativas(
        locales: List<LocalEntity>,
        criterios: CriteriosBusqueda,
        perfil: Map<String, Int>,
        lat: Double,
        lon: Double,
        maxAlternativas: Int = 3,
        horasHolgura: Int = 3,
        radioMaxKm: Double = 20.0
    ): List<Propuesta> {
        val maxAf = maxAfinidad(perfil)
        val radio = maxOf(criterios.radioKm, radioMaxKm)
        val fecha = fechaDe(criterios)
        return locales.mapNotNull { local ->
            if (!local.dias.contains(criterios.dia.toString()) || local.capacidad < criterios.personas) return@mapNotNull null
            if (!vigenteEl(local, fecha)) return@mapNotNull null
            val km = distanciaKm(lat, lon, local.latitud, local.longitud)
            if (km > radio) return@mapNotNull null
            // Hora más cercana a la pedida dentro del horario del local.
            val hora = criterios.hora.coerceIn(local.horaInicio, local.horaFin - 1)
            val diferencia = kotlin.math.abs(hora - criterios.hora)
            if (diferencia > horasHolgura) return@mapNotNull null

            val cambios = mutableListOf<String>()
            if (diferencia > 0) cambios += "a las %02d:00".format(hora)
            if (km > criterios.radioKm) cambios += "a %.1f km".format(km).replace('.', ',')
            val otraComida = criterios.preferencia != TODAS && local.categoria != criterios.preferencia
            if (otraComida) cambios += "es ${local.categoria.lowercase()}"
            if (cambios.isEmpty()) return@mapNotNull null // calza exacto: ya está en las propuestas

            val (base, razones) = evaluar(local, km, criterios.copy(hora = hora, radioKm = radio), perfil, maxAf)
            val penalizacion = diferencia * 4 + (if (km > criterios.radioKm) 8 else 0) + (if (otraComida) 10 else 0)
            Propuesta(
                local, (base - penalizacion).coerceIn(1, 99), km, razones,
                ajuste = cambios.joinToString(", ").replaceFirstChar { it.uppercase() }
            )
        }.sortedWith(compareByDescending<Propuesta> { it.match }.thenBy { it.distanciaKm })
            .distinctBy { restaurante(it.local) }
            .take(maxAlternativas)
    }
}

// ---------- Formatos de presentación ----------

private val NOMBRES_DIA = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val CORTOS_DIA = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

fun nombreDia(dia: Int) = NOMBRES_DIA[(dia - 1).coerceIn(0, 6)]
fun diaCorto(dia: Int) = CORTOS_DIA[(dia - 1).coerceIn(0, 6)]

/** "1234" -> "Lun–Jue"; "135" -> "Lun, Mié, Vie"; "1234567" -> "Todos los días". */
fun textoDias(dias: String): String {
    val nums = dias.mapNotNull { it.digitToIntOrNull() }.filter { it in 1..7 }.distinct().sorted()
    if (nums.isEmpty()) return "Sin días"
    if (nums.size == 7) return "Todos los días"
    val consecutivos = nums.zipWithNext().all { (a, b) -> b == a + 1 }
    return if (consecutivos && nums.size > 2) "${diaCorto(nums.first())}–${diaCorto(nums.last())}"
    else nums.joinToString(", ") { diaCorto(it) }
}

/** "todos los viernes", "todos los días" o "los días Lun–Jue". */
fun textoDiasLargo(dias: String): String {
    val nums = dias.mapNotNull { it.digitToIntOrNull() }.filter { it in 1..7 }.distinct()
    return when {
        nums.size == 7 -> "todos los días"
        nums.size == 1 -> "todos los " + plural(nums.first())
        else -> "los días ${textoDias(dias)}"
    }
}

/** "viernes", "sábados", "domingos": cómo se dice cuando se repite cada semana. */
fun plural(dia: Int): String = nombreDia(dia).lowercase().let { if (it.endsWith("s")) it else it + "s" }

/** "vie 28 nov". */
fun fechaCorta(fecha: Long) = "${diaCorto(diaSemana(fecha)).lowercase()} ${numeroDia(fecha)} ${mesCorto(fecha)}"

/** true si la promoción ya pasó su fecha de término. */
fun terminada(local: LocalEntity, hoy: Long = inicioDelDia()) = local.hasta in 1 until hoy

/** true si la promoción todavía no empieza. */
fun programada(local: LocalEntity, hoy: Long = inicioDelDia()) = local.desde > hoy

/** Vigencia en palabras para tarjetas: "Hasta el dom 28 dic", "Desde el vie 28 nov"; null si no tiene fechas. */
fun textoVigencia(local: LocalEntity, hoy: Long = inicioDelDia()): String? = when {
    terminada(local, hoy) -> "Terminó el ${fechaCorta(local.hasta)}"
    programada(local, hoy) && local.hasta > 0 -> "Del ${fechaCorta(local.desde)} al ${fechaCorta(local.hasta)}"
    programada(local, hoy) -> "Desde el ${fechaCorta(local.desde)}"
    local.hasta > 0 -> "Hasta el ${fechaCorta(local.hasta)}"
    else -> null
}

/** Cuántas veces cae la promoción entre dos fechas (ambas incluidas). */
fun vecesEnPeriodo(dias: String, desde: Long, hasta: Long): Int {
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = desde }
    var veces = 0
    var i = 0
    while (cal.timeInMillis <= hasta && i < 800) {
        if (dias.contains(diaSemana(cal.timeInMillis).toString())) veces++
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        i++
    }
    return veces
}

fun textoHorario(local: LocalEntity) =
    textoDias(local.dias) + " · %02d:00–%02d:00".format(local.horaInicio, local.horaFin)

/** 11900 -> "$11.900" (formato chileno). */
fun pesos(monto: Int): String = "$" + "%,d".format(monto).replace(',', '.')

/** Código de canje legible y estable para un canje. */
fun codigoCanje(localId: Long, fecha: Long): String =
    "CC-%02d%04d".format(localId % 100, fecha % 10000)
