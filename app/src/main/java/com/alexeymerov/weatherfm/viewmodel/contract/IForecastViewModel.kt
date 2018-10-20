package com.alexeymerov.weatherfm.viewmodel.contract

import android.app.Application
import androidx.lifecycle.LiveData
import com.alexeymerov.weatherfm.data.database.entity.ForecastEntity
import com.alexeymerov.weatherfm.viewmodel.BaseViewModel
import com.google.android.gms.maps.model.LatLng

abstract class IForecastViewModel(application: Application) : BaseViewModel(application) {

    abstract val forecastList: LiveData<List<ForecastEntity>>

    abstract fun requestForecast()

    abstract fun updateCurrentCity(latLan: LatLng)

}