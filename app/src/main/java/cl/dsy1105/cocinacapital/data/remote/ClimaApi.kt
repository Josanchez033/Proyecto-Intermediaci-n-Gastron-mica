package cl.dsy1105.cocinacapital.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// API EXTERNA: Open-Meteo (https://open-meteo.com), pronóstico del tiempo gratuito y sin clave.
// Va aparte de los microservicios propios: no toca Room ni el backend de Cocina Capital.
// Datos bajo licencia CC BY 4.0: la app muestra "Datos: Open-Meteo".

data class ClimaRespuesta(
    val current: ClimaActual?,
    val daily: ClimaDiario?
)

data class ClimaActual(
    @SerializedName("temperature_2m") val temperatura: Double,
    @SerializedName("weather_code") val codigo: Int,
    @SerializedName("is_day") val esDia: Int
)

data class ClimaDiario(
    @SerializedName("temperature_2m_max") val maximas: List<Double>?,
    @SerializedName("temperature_2m_min") val minimas: List<Double>?,
    @SerializedName("precipitation_probability_max") val probLluvia: List<Int?>?
)

interface ClimaApi {
    /** Clima actual y del día para un punto. Ej.: /forecast?latitude=-33.05&longitude=-71.62&... */
    @GET("forecast")
    suspend fun pronostico(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") actual: String = "temperature_2m,weather_code,is_day",
        @Query("daily") diario: String = "temperature_2m_max,temperature_2m_min,precipitation_probability_max",
        @Query("timezone") zona: String = "America/Santiago",
        @Query("forecast_days") dias: Int = 1
    ): ClimaRespuesta

    companion object {
        const val URL = "https://api.open-meteo.com/v1/"

        val instancia: ClimaApi by lazy {
            Retrofit.Builder()
                .baseUrl(URL)
                .client(OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(8, TimeUnit.SECONDS).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ClimaApi::class.java)
        }
    }
}
