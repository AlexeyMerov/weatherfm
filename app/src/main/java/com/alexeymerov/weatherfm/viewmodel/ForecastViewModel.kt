package com.alexeymerov.weatherfm.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.alexeymerov.weatherfm.data.database.entity.ForecastEntity
import com.alexeymerov.weatherfm.data.repository.contracts.IForecastRepository
import com.alexeymerov.weatherfm.viewmodel.contract.IForecastViewModel
import com.google.android.gms.maps.model.LatLng

class ForecastViewModel(application: Application, private val repository: IForecastRepository) :
    IForecastViewModel(application) {

    override val forecastList: LiveData<List<ForecastEntity>> = repository.forecastList

    override fun requestForecast() = repository.requestForecast()

    override fun updateCurrentCity(latLan: LatLng) = repository.updateCurrentCity(latLan)

    override fun onCleared() {
        repository.onCleared()
        super.onCleared()
    }
}