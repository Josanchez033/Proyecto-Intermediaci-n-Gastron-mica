package cl.dsy1105.cocinacapital.utils

import cl.dsy1105.cocinacapital.data.diaCorto
import cl.dsy1105.cocinacapital.data.nombreDia
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.roundToInt

// Fechas de la búsqueda ("Arma tu salida"). Se usa Calendar porque la app parte en Android 7 (API 24).

const val PRIMERA_HORA = 8
const val ULTIMA_HORA = 22
/** Hasta cuántos días adelante se puede armar una salida desde el calendario. */
const val DIAS_CALENDARIO = 60

private val MESES = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
private const val DIA_MS = 86_400_000L

/** Medianoche local de hoy + [offset] días, en milisegundos. */
fun inicioDelDia(offset: Int = 0): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    add(Calendar.DAY_OF_YEAR, offset)
}.timeInMillis

/** Días entre hoy y [fecha] (0 = hoy). Redondea para no fallar con el cambio de horario. */
fun diasDesdeHoy(fecha: Long): Int = ((fecha - inicioDelDia()).toDouble() / DIA_MS).roundToInt()

/** Día de la semana de la app: 1 = lunes ... 7 = domingo. */
fun diaSemana(fecha: Long): Int {
    val cal = Calendar.getInstance().apply { timeInMillis = fecha }
    return (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1
}

fun numeroDia(fecha: Long): Int = Calendar.getInstance().apply { timeInMillis = fecha }.get(Calendar.DAY_OF_MONTH)

fun mesCorto(fecha: Long): String = MESES[Calendar.getInstance().apply { timeInMillis = fecha }.get(Calendar.MONTH)]

fun sumarDias(fecha: Long, dias: Int): Long =
    Calendar.getInstance().apply { timeInMillis = fecha; add(Calendar.DAY_OF_YEAR, dias) }.timeInMillis

/** Último día de un período de [meses] meses que parte en [fecha] (ej. 28 nov + 1 mes = 27 dic). */
fun finDeMeses(fecha: Long, meses: Int): Long =
    Calendar.getInstance().apply { timeInMillis = fecha; add(Calendar.MONTH, meses); add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis

/** Próxima fecha (hoy incluido) que cae en el día de la semana [dia]. */
fun proximaFecha(dia: Int): Long = (0..6).map { inicioDelDia(it) }.first { diaSemana(it) == dia }

/** "Hoy", "Mañana" o "Sáb 28 nov". */
fun textoFechaCorta(fecha: Long): String = when (diasDesdeHoy(fecha)) {
    0 -> "Hoy"
    1 -> "Mañana"
    else -> "${diaCorto(diaSemana(fecha))} ${numeroDia(fecha)} ${mesCorto(fecha)}"
}

/** "Hoy domingo", "Mañana lunes" o "Sábado 28 de noviembre". */
fun textoFechaLarga(fecha: Long): String {
    val nombre = nombreDia(diaSemana(fecha))
    return when (diasDesdeHoy(fecha)) {
        0 -> "Hoy ${nombre.lowercase()}"
        1 -> "Mañana ${nombre.lowercase()}"
        else -> "$nombre ${numeroDia(fecha)} de ${MESES_LARGOS[Calendar.getInstance().apply { timeInMillis = fecha }.get(Calendar.MONTH)]}"
    }
}

private val MESES_LARGOS = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
)

/** Horas que todavía se pueden elegir ese día: si es hoy, desde la hora actual en adelante. */
fun horasDisponibles(fecha: Long, horaActual: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): IntRange =
    if (diasDesdeHoy(fecha) <= 0) maxOf(horaActual, PRIMERA_HORA)..ULTIMA_HORA else PRIMERA_HORA..ULTIMA_HORA

// El calendario de Material trabaja con la medianoche en UTC; estas dos funciones traducen.

fun aUtc(fecha: Long): Long {
    val local = Calendar.getInstance().apply { timeInMillis = fecha }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear(); set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

fun desdeUtc(utc: Long): Long {
    val u = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utc }
    return Calendar.getInstance().apply {
        clear(); set(u.get(Calendar.YEAR), u.get(Calendar.MONTH), u.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}
