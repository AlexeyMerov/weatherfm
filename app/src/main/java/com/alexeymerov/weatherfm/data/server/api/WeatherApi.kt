package com.alexeymerov.weatherfm.data.server.api

import com.alexeymerov.weatherfm.WEATHER_API_KEY
import com.alexeymerov.weatherfm.data.server.pojo.response.ForecastResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApiService {

    @GET("forecast")
    fun getForecast(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String = "metric",
        @Query("appid") appid: String = WEATHER_API_KEY
    ): Single<ForecastResponse>

}
