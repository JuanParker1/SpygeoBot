package data.remote.api

import data.remote.models.ReversedCountry
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ReversedGeocodingApi {

    @GET("reverse")
    fun getCountryNameByCoordinates(
        @Query("lat") latitude: String,
        @Query("lon") longtitude: String,
        @Query("format") formatData: String
    ): Deferred<ReversedCountry>
}