package cl.dsy1105.cocinacapital.data.remote

import cl.dsy1105.cocinacapital.data.room.CanjeEntity
import cl.dsy1105.cocinacapital.data.room.ConteoCanjes
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.room.PreferenciaEntity
import cl.dsy1105.cocinacapital.data.room.RestauranteEntity
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Conexión con los microservicios propios (Spring Boot). Ver backend/README.md.
// 10.0.2.2 es el "localhost" del computador visto desde el emulador de Android.
object ApiConfig {
    const val USUARIOS_URL = "http://10.0.2.2:8081/api/"
    const val PROMOCIONES_URL = "http://10.0.2.2:8082/api/"
}

// ---------- Cuerpos y respuestas (mismos nombres de campo que el backend) ----------

data class UsuarioDto(
    val email: String, val rol: String, val activo: Boolean,
    val nombre: String?, val foto: String?, val claveTemporal: Boolean
)
data class LoginBody(val email: String, val password: String)
data class RegistroBody(val email: String, val password: String, val nombre: String? = null)
data class NuevoLocalBody(val email: String, val password: String, val nombre: String, val comuna: String, val direccion: String)
data class CuentaLocalDto(val usuario: UsuarioDto, val restaurante: RestauranteEntity?)
data class ActivoBody(val activo: Boolean)
data class ClaveTemporalBody(val temporal: String)
data class PerfilBody(val nombre: String?, val foto: String?)
data class CambioClaveBody(val actual: String?, val nueva: String)
data class EstadoBody(val activa: Boolean)
data class CanjeBody(val emailUsuario: String, val localId: Long)
data class PuntosBody(val emailUsuario: String, val categoria: String, val puntos: Int)

/** Microservicio de usuarios (puerto 8081). */
interface UsuariosApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): UsuarioDto

    @POST("auth/registro")
    suspend fun registrar(@Body body: RegistroBody): UsuarioDto

    @GET("auth/existe")
    suspend fun existe(@Query("email") email: String): Map<String, Boolean>

    /** Recuperar contraseña: la app ya verificó el código de 6 dígitos. */
    @POST("auth/restablecer")
    suspend fun restablecer(@Body body: LoginBody): Response<Unit>

    @PUT("usuarios/{email}")
    suspend fun actualizarPerfil(@Path("email") email: String, @Body body: PerfilBody): UsuarioDto

    @PUT("usuarios/{email}/clave")
    suspend fun cambiarClave(@Path("email") email: String, @Body body: CambioClaveBody): Response<Unit>

    @GET("usuarios/locales")
    suspend fun locales(): List<CuentaLocalDto>

    @POST("usuarios/locales")
    suspend fun crearLocal(@Body body: NuevoLocalBody): CuentaLocalDto

    @PATCH("usuarios/{email}/activo")
    suspend fun cambiarActivo(@Path("email") email: String, @Body body: ActivoBody): UsuarioDto

    @PUT("usuarios/{email}/clave-temporal")
    suspend fun claveTemporal(@Path("email") email: String, @Body body: ClaveTemporalBody): UsuarioDto

    @GET("restaurantes")
    suspend fun restaurantes(): List<RestauranteEntity>

    @PUT("restaurantes/{email}")
    suspend fun actualizarRestaurante(@Path("email") email: String, @Body body: RestauranteEntity): RestauranteEntity
}

/** Microservicio de promociones, canjes y perfil de consumo (puerto 8082). */
interface PromocionesApi {
    @GET("promociones")
    suspend fun promociones(): List<LocalEntity>

    @POST("promociones")
    suspend fun crear(@Body promo: LocalEntity): LocalEntity

    @PUT("promociones/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body promo: LocalEntity): LocalEntity

    @PATCH("promociones/{id}/estado")
    suspend fun cambiarEstado(@Path("id") id: Long, @Body body: EstadoBody): LocalEntity

    @DELETE("promociones/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>

    @GET("canjes")
    suspend fun canjes(@Query("usuario") email: String): List<CanjeEntity>

    @POST("canjes")
    suspend fun canjear(@Body body: CanjeBody): CanjeEntity

    @GET("canjes/conteo")
    suspend fun conteo(@Query("vendedor") email: String): List<ConteoCanjes>

    @GET("preferencias")
    suspend fun preferencias(@Query("usuario") email: String): List<PreferenciaEntity>

    @POST("preferencias/sumar")
    suspend fun sumarPuntos(@Body body: PuntosBody): PreferenciaEntity
}

object RetrofitClient {
    // Tiempos cortos: si el backend no está, la app sigue con sus datos guardados sin hacer esperar.
    private val http by lazy {
        OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    private fun retrofit(url: String) = Retrofit.Builder()
        .baseUrl(url)
        .client(http)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usuarios: UsuariosApi by lazy { retrofit(ApiConfig.USUARIOS_URL).create(UsuariosApi::class.java) }
    val promociones: PromocionesApi by lazy { retrofit(ApiConfig.PROMOCIONES_URL).create(PromocionesApi::class.java) }
}
