package com.alexeymerov.weatherfm.data.server.pojo.response


import androidx.room.Entity
import com.squareup.moshi.Json

@Entity
data class ForecastResponse(
    @Json(name = "city") val city: CityResponse,
    @Json(name = "list") val list: List<ListItemResponse>
)

data class CityResponse(
    @Json(name = "country") val countryIso: String,
    @Json(name = "name") val name: String
)

data class ListItemResponse(
    @Json(name = "dt") val dateTimestamp: Int,
    @Json(name = "weather") val weather: List<WeatherResponse>,
    @Json(name = "main") val main: MainResponse,
    @Json(name = "clouds") val clouds: CloudsResponse,
    @Json(name = "wind") val wind: WindResponse
)

data class MainResponse(
    @Json(name = "temp") val temp: Double,
    @Json(name = "grnd_level") val pressure: Double,
    @Json(name = "humidity") val humidity: Int
)

data class WeatherResponse(
    @Json(name = "main") val type: String
)

data class WindResponse(
    @Json(name = "deg") val degree: Double,
    @Json(name = "speed") val speed: Double
)

data class CloudsResponse(
    @Json(name = "all") val percent: Int
)