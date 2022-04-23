package data.remote

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import data.remote.api.ReversedGeocodingApi
import data.remote.api.WeatherApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val WEATHER_BASE_URL = "http://api.weatherapi.com/v1/"
const val REVERSE_GEOCODER_BASE_URL = "https://nominatim.openstreetmap.org/"
const val WEATHER_API_KEY = "e78286547711edf4599ce491dcf2ba24"

enum class RetrofitType(val baseUrl: String) {
    WEATHER(WEATHER_BASE_URL),
    REVERSE_GEOCODER(REVERSE_GEOCODER_BASE_URL)
}

object RetrofitClient {

    private fun getClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return httpClient.build()
    }

    fun getRetrofit(retrofitType: RetrofitType): Retrofit {
        return Retrofit.Builder()
            .baseUrl(retrofitType.baseUrl)
            .client(getClient())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    fun getReversedGeocodingApi(retrofit: Retrofit): ReversedGeocodingApi {
        return retrofit.create(ReversedGeocodingApi::class.java)
    }

}
