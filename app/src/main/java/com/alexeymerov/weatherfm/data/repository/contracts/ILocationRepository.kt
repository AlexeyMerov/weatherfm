package com.alexeymerov.weatherfm.data.repository.contracts

import com.alexeymerov.weatherfm.data.repository.BaseRepository
import org.koin.standalone.KoinComponent

abstract class ILocationRepository : BaseRepository(), KoinComponent {

    abstract fun searchCities(query: String, onResult: (List<String>) -> Unit)

}