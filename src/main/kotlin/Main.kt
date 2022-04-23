import bot.WeatherBot
import data.remote.RetrofitClient
import data.remote.RetrofitType
import data.remote.repository.WeatherRepository

fun main() {
    val weatherRetrofitClient = RetrofitClient.getRetrofit(RetrofitType.WEATHER)
    val reverseGeocoderRetrofitClient = RetrofitClient.getRetrofit(RetrofitType.REVERSE_GEOCODER)

    val bot = WeatherBot(
        WeatherRepository(
            weatherApi = RetrofitClient.getWeatherApi(weatherRetrofitClient),
            reversedGeocodingApi = RetrofitClient.getReversedGeocodingApi(reverseGeocoderRetrofitClient)
        )
    ).createBot()

    bot.startPolling()
}