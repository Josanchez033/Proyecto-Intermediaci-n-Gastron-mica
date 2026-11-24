package cl.dsy1105.cocinacapital.data.remote

import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException

/** Resultado de una llamada a los microservicios. */
sealed interface Remoto<out T> {
    data class Ok<T>(val valor: T) : Remoto<T>

    /** El servidor respondió con un error (401, 403, 404, 409...). [mensaje] viene de {"error": "..."}. */
    data class Error(val codigo: Int, val mensaje: String) : Remoto<Nothing>

    /** No hubo respuesta: el backend no está corriendo o no hay internet. */
    data object SinConexion : Remoto<Nothing>
}

/** Ejecuta una llamada y traduce las excepciones de red a un [Remoto], sin dejar que la app se caiga. */
suspend fun <T> llamar(bloque: suspend () -> T): Remoto<T> = try {
    Remoto.Ok(bloque())
} catch (e: HttpException) {
    Remoto.Error(e.code(), mensajeDeError(e))
} catch (_: IOException) {
    Remoto.SinConexion
} catch (e: kotlinx.coroutines.CancellationException) {
    throw e // si se cancela la corrutina, se respeta la cancelación
} catch (_: Exception) {
    // JSON inesperado u otro error: se trata como sin conexión para seguir con los datos locales.
    Remoto.SinConexion
}

private fun mensajeDeError(e: HttpException): String = runCatching {
    val cuerpo = e.response()?.errorBody()?.string().orEmpty()
    JsonParser.parseString(cuerpo).asJsonObject.get("error").asString
}.getOrDefault("Error ${e.code()} del servidor")
