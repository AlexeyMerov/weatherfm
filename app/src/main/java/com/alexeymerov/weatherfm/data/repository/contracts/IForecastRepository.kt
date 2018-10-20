package com.alexeymerov.weatherfm.data.repository.contracts

import androidx.lifecycle.LiveData
import com.alexeymerov.weatherfm.data.database.entity.ForecastEntity
import com.alexeymerov.weatherfm.data.repository.BaseRepository
import com.google.android.gms.maps.model.LatLng
import org.koin.standalone.KoinComponent

abstract class IForecastRepository : BaseRepository(), KoinComponent {

    abstract val forecastList: LiveData<List<ForecastEntity>>

    abstract fun requestForecast()

    abstract fun updateCurrentCity(latLan: LatLng)

}