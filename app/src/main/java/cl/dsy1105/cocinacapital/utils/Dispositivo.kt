package cl.dsy1105.cocinacapital.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.LocationManager
import androidx.core.app.NotificationCompat
import cl.dsy1105.cocinacapital.data.room.LocalEntity

// Última ubicación conocida del dispositivo (requiere permiso de ubicación).
@SuppressLint("MissingPermission")
fun obtenerUbicacion(context: Context): Pair<Double, Double>? {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return lm.getProviders(true)
        .mapNotNull { runCatching { lm.getLastKnownLocation(it) }.getOrNull() }
        .firstOrNull()?.let { it.latitude to it.longitude }
}

// Notificación local con la promoción que mejor calza con el perfil del usuario.
fun mostrarAlertaPromocion(context: Context, local: LocalEntity) {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    nm.createNotificationChannel(
        NotificationChannel("promos", "Promociones", NotificationManager.IMPORTANCE_DEFAULT)
    )
    val n = NotificationCompat.Builder(context, "promos")
        .setSmallIcon(android.R.drawable.star_on)
        .setContentTitle("${local.emoji} ${local.nombre}: -${local.descuento}%")
        .setContentText("${local.promocion} · ${local.horario}")
        .build()
    nm.notify(1, n)
}
