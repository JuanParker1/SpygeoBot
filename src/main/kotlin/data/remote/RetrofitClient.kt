package data.remote

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val WEATHER_BASE_URL = "http://api.weatherapi.com/v1/"
const val REVERSE_GEOCODER_BASE_URL = "https://nominatim.openstreetmap.org/"
const val WEATHER_API_KEY = "e78286547711edf4599ce491dcf2ba24"

enum class RetrofitType(val baseUrl: String) {
    WEATHER(WEATHER_BASE_URL),
    REVERSE_GEOCODER(REVERSE_GEOCODER_BASE_URL)
}
class RetrofitClient {

    fun getClient(retrofitType: RetrofitType): Retrofit {
        return Retrofit.Builder()
            .baseUrl(retrofitType.baseUrl)
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
