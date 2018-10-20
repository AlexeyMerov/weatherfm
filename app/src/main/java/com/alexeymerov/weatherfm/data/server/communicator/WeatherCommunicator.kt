package com.alexeymerov.weatherfm.data.server.communicator

import com.alexeymerov.weatherfm.data.server.api.WeatherApiService

class WeatherCommunicator(apiUrl: String) : BaseCommunicator<WeatherApiService>(WeatherApiService::class.java, apiUrl) {

    fun updateForecast(lat: String, lon: String) = apiService.getForecast(lat, lon)

}