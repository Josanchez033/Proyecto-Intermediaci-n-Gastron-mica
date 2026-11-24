package cl.dsy1105.cocinacapital.data.remote

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/** Cómo se traducen las respuestas y fallas de red de los microservicios. */
class RemotoTest {

    private fun http(codigo: Int, cuerpo: String) =
        HttpException(Response.error<Any>(codigo, ResponseBody.create(MediaType.parse("application/json"), cuerpo)))

    @Test
    fun respuestaCorrecta() = runTest {
        assertEquals(Remoto.Ok(42), llamar { 42 })
    }

    @Test
    fun errorDelServidor_conMensaje() = runTest {
        val r = llamar<Int> { throw http(409, """{"error":"Ese correo ya tiene una cuenta"}""") }
        assertEquals(Remoto.Error(409, "Ese correo ya tiene una cuenta"), r)
    }

    @Test
    fun errorDelServidor_sinCuerpoJson() = runTest {
        assertEquals(Remoto.Error(500, "Error 500 del servidor"), llamar<Int> { throw http(500, "caído") })
    }

    @Test
    fun sinRed_oRespuestaRara_esSinConexion() = runTest {
        assertEquals(Remoto.SinConexion, llamar<Int> { throw IOException("sin red") })
        assertEquals(Remoto.SinConexion, llamar<Int> { throw IllegalStateException("JSON inesperado") })
    }

    @Test(expected = CancellationException::class)
    fun laCancelacionNoSeTraga() = runTest {
        llamar<Int> { throw CancellationException("cancelada") }
    }
}
