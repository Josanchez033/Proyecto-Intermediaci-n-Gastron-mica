package cl.dsy1105.cocinacapital.data

import cl.dsy1105.cocinacapital.data.remote.ClimaRespuesta
import kotlin.math.roundToInt

// Lógica del clima SIN dependencias de Android: se prueba con JUnit.

/** Tipo de cielo, agrupando los códigos WMO que entrega Open-Meteo. */
enum class Cielo { DESPEJADO, NUBLADO, NIEBLA, LLOVIZNA, LLUVIA, TORMENTA, NIEVE }

data class Clima(
    val temperatura: Int,
    val cielo: Cielo,
    val esDia: Boolean,
    val maxima: Int?,
    val minima: Int?,
    val probLluvia: Int?
) {
    /** "Despejado", "Parcialmente nublado"... */
    val descripcion: String get() = when (cielo) {
        Cielo.DESPEJADO -> if (esDia) "Despejado" else "Noche despejada"
        Cielo.NUBLADO -> "Nublado"
        Cielo.NIEBLA -> "Neblina"
        Cielo.LLOVIZNA -> "Llovizna"
        Cielo.LLUVIA -> "Lluvia"
        Cielo.TORMENTA -> "Tormenta"
        Cielo.NIEVE -> "Nieve"
    }

    /** true si conviene comer adentro: llueve, hay tormenta o hace frío. */
    val mejorAdentro: Boolean get() =
        cielo in setOf(Cielo.LLOVIZNA, Cielo.LLUVIA, Cielo.TORMENTA, Cielo.NIEVE) || temperatura < 12 || (probLluvia ?: 0) >= 60

    /** Sugerencia para la salida según el clima: conecta el pronóstico con las promociones. */
    val sugerencia: String get() = when {
        cielo == Cielo.TORMENTA || cielo == Cielo.LLUVIA -> "Día de lluvia: busca una mesa bajo techo y algo calentito."
        (probLluvia ?: 0) >= 60 -> "Puede llover más tarde: mejor una mesa adentro."
        temperatura < 12 -> "Está helado: un café o una cazuela caen bien."
        !esDia -> "Buena noche para salir a comer."
        temperatura >= 24 -> "Hace calor: ideal para terraza, helado o algo fresco."
        cielo == Cielo.DESPEJADO -> "Día despejado: buen momento para una terraza con vista."
        else -> "Buen día para salir a comer."
    }

    companion object {
        /** Códigos WMO: 0 despejado, 1-3 nubes, 45-48 niebla, 51-57 llovizna, 61-67 y 80-82 lluvia, 71-77 y 85-86 nieve, 95-99 tormenta. */
        fun cieloDe(codigo: Int): Cielo = when (codigo) {
            0 -> Cielo.DESPEJADO
            1, 2, 3 -> Cielo.NUBLADO
            45, 48 -> Cielo.NIEBLA
            in 51..57 -> Cielo.LLOVIZNA
            in 61..67, in 80..82 -> Cielo.LLUVIA
            in 71..77, 85, 86 -> Cielo.NIEVE
            in 95..99 -> Cielo.TORMENTA
            else -> Cielo.NUBLADO
        }

        /** Convierte la respuesta de Open-Meteo; null si no trae el clima actual. */
        fun desde(r: ClimaRespuesta): Clima? {
            val actual = r.current ?: return null
            return Clima(
                temperatura = actual.temperatura.roundToInt(),
                cielo = cieloDe(actual.codigo),
                esDia = actual.esDia == 1,
                maxima = r.daily?.maximas?.firstOrNull()?.roundToInt(),
                minima = r.daily?.minimas?.firstOrNull()?.roundToInt(),
                probLluvia = r.daily?.probLluvia?.firstOrNull()
            )
        }
    }
}
