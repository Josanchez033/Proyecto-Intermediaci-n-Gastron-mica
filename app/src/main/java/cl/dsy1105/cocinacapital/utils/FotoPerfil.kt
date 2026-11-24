package cl.dsy1105.cocinacapital.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

// Foto de perfil: se copia al almacenamiento interno de la app (reducida a 512 px),
// así no depende del permiso temporal que entrega el selector de fotos del sistema.

/** Copia la imagen elegida y devuelve la ruta guardada, o null si no se pudo leer. */
fun guardarFotoPerfil(context: Context, uri: Uri, email: String): String? = runCatching {
    val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
    var muestra = 1
    while (maxOf(opciones.outWidth, opciones.outHeight) / (muestra * 2) >= 512) muestra *= 2
    val bitmap = context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = muestra })
    } ?: return null
    val escala = 512f / maxOf(bitmap.width, bitmap.height)
    val final = if (escala < 1f)
        Bitmap.createScaledBitmap(bitmap, (bitmap.width * escala).toInt(), (bitmap.height * escala).toInt(), true)
    else bitmap
    // Nombre nuevo en cada cambio para que la interfaz no muestre la foto anterior en caché.
    val carpeta = File(context.filesDir, "perfiles").apply { mkdirs() }
    carpeta.listFiles { f -> f.name.startsWith(nombreSeguro(email)) }?.forEach { it.delete() }
    val archivo = File(carpeta, "${nombreSeguro(email)}_${System.currentTimeMillis()}.jpg")
    archivo.outputStream().use { final.compress(Bitmap.CompressFormat.JPEG, 85, it) }
    archivo.absolutePath
}.getOrNull()

fun borrarFotoPerfil(ruta: String?) {
    if (ruta != null) runCatching { File(ruta).delete() }
}

private fun nombreSeguro(email: String) = email.lowercase().replace(Regex("[^a-z0-9]"), "_")
