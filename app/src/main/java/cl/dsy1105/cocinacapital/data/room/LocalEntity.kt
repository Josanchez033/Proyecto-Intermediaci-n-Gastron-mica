package cl.dsy1105.cocinacapital.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Cada fila de la tabla "locales": un local con su promoción de baja demanda.
// Esta misma clase se usa como modelo del JSON que entrega la API REST.
@Entity(tableName = "locales")
data class LocalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val categoria: String,
    val comuna: String,
    val direccion: String,
    val promocion: String,
    val incluye: String,
    val descripcion: String,
    val descuento: Int,
    val precioNormal: Int,
    // Días en que aplica la promoción: dígitos 1 (lunes) a 7 (domingo), ej. "1234".
    val dias: String,
    val horaInicio: Int,          // 15 = 15:00
    val horaFin: Int,             // 19 = 19:00 (exclusiva)
    val capacidad: Int,           // personas que puede recibir con la promo
    val rating: Double,
    val latitud: Double,
    val longitud: Double,
    val foto: String,             // nombre del recurso en drawable-nodpi
    val vendedorEmail: String? = null, // null = catálogo de Cocina Capital
    // El Local puede pausar una promoción: deja de verse, pero queda en su historial.
    @ColumnInfo(defaultValue = "1") val activa: Boolean = true,
    @ColumnInfo(defaultValue = "0") val creada: Long = 0, // fecha de publicación (ms)
    // Tipo de oferta: Oferta.PORCENTAJE, DOS_POR_UNO (2x1) o TRES_POR_DOS (3x2).
    @ColumnInfo(defaultValue = "0") val oferta: Int = Oferta.PORCENTAJE,
    // Vigencia programada por el Local (medianoche local, en ms). 0 = sin límite:
    // empieza ya y sigue hasta que el Local la pause.
    @ColumnInfo(defaultValue = "0") val desde: Long = 0,
    @ColumnInfo(defaultValue = "0") val hasta: Long = 0,
    // Nombre corto de una oferta propia del Local ("Combo", "Menú del día"); solo con Oferta.PERSONALIZADA.
    @ColumnInfo(defaultValue = "''") val etiqueta: String = "",
    // Precio exacto que fijó el Local en una oferta propia (0 = se calcula con el descuento).
    @ColumnInfo(defaultValue = "0") val precioOferta: Int = 0
) {
    val precioPromo: Int get() = if (precioOferta > 0) precioOferta else precioNormal * (100 - descuento) / 100

    /** Lo que muestra la etiqueta verde: "-30%", "2x1" o "3x2". */
    val etiquetaOferta: String get() = when (oferta) {
        Oferta.DOS_POR_UNO -> "2x1"
        Oferta.TRES_POR_DOS -> "3x2"
        Oferta.PERSONALIZADA -> etiqueta.ifBlank { "-$descuento%" }
        else -> "-$descuento%"
    }
}

object Oferta {
    const val PORCENTAJE = 0
    const val DOS_POR_UNO = 2   // lleva 2, paga 1: equivale a 50% por unidad
    const val TRES_POR_DOS = 3  // lleva 3, paga 2: equivale a 33% por unidad
    const val PERSONALIZADA = 9 // la arma el Local: combo, menú, precio especial (nombre y precio propios)

    /** Descuento por unidad con que se calcula el precio y el match. */
    fun descuentoDe(oferta: Int, porcentaje: Int) = when (oferta) {
        DOS_POR_UNO -> 50
        TRES_POR_DOS -> 33
        else -> porcentaje
    }
}
