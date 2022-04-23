package data.remote.models

data class ReversedCountry(
    val address: Address,
    val `class`: String,
    val display_name: String,
    val extratags: Extratags,
    val importance: String,
    val lat: String,
    val licence: String,
    val lon: String,
    val osm_id: String,
    val osm_type: String,
    val place_id: String,
    val type: String
)