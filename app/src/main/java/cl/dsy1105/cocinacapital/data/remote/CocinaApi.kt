package cl.dsy1105.cocinacapital.data.remote

import cl.dsy1105.cocinacapital.data.room.LocalEntity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Backend de intermediación (API REST en Spring Boot).
// 10.0.2.2 es el "localhost" de tu PC visto desde el emulador de Android.
interface CocinaApi {
    @GET("ofertas")
    suspend fun obtenerOfertas(): List<LocalEntity>
}

object RetrofitClient {
    val api: CocinaApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CocinaApi::class.java)
    }
}
