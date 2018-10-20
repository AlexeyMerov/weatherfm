package com.alexeymerov.weatherfm.data.repository

import androidx.lifecycle.LiveData
import com.alexeymerov.weatherfm.SP_LAST_TIME_MINUTES
import com.alexeymerov.weatherfm.SP_LAT
import com.alexeymerov.weatherfm.SP_LON
import com.alexeymerov.weatherfm.data.database.WeatherFmDatabase
import com.alexeymerov.weatherfm.data.database.entity.ForecastEntity
import com.alexeymerov.weatherfm.data.repository.contracts.IForecastRepository
import com.alexeymerov.weatherfm.data.server.communicator.WeatherCommunicator
import com.alexeymerov.weatherfm.data.server.pojo.response.ListItemResponse
import com.alexeymerov.weatherfm.utils.FieldType
import com.alexeymerov.weatherfm.utils.SPHelper
import com.alexeymerov.weatherfm.utils.errorLog
import com.alexeymerov.weatherfm.utils.extensions.currentMinutes
import com.alexeymerov.weatherfm.utils.getFieldInt
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable


class ForecastRepository(
    private val weatherCommunicator: WeatherCommunicator,
    private val database: WeatherFmDatabase
) : IForecastRepository() {

    override val forecastList: LiveData<List<ForecastEntity>> = database.forecastDao().getAllLive()

    override fun requestForecast() {
        val lat = SPHelper.getShared(SP_LAT, "")
        val lon = SPHelper.getShared(SP_LON, "")
        if (lat.isEmpty() || lon.isEmpty()) return

        val lastTimeMinutes = SPHelper.getShared(SP_LAST_TIME_MINUTES, 0L)
        if (lastTimeMinutes != 0L && currentMinutes() - lastTimeMinutes < 10) return

        SPHelper.setShared(SP_LAST_TIME_MINUTES, currentMinutes())
        runDisposable {
            weatherCommunicator
                .updateForecast(lat, lon)
                .flatMapObservable { Observable.fromIterable(it.list) }
                .map { convertToEntity(it) }
                .toList()
                .compose(singleTransformer())
                .doOnError { errorLog(it) }
                .subscribe { entityList ->
                    database.forecastDao().removeAll()
                    database.forecastDao().add(entityList)
                }
        }
    }

    private fun convertToEntity(it: ListItemResponse): ForecastEntity {
        return it.run {
            val timeMillis = dateTimestamp.toLong() * 1000
            ForecastEntity(
                timeMillis,
                timeMillis.getFieldInt(FieldType.DAY),
                timeMillis.getFieldInt(FieldType.MONTH),
                clouds.percent,
                wind.speed.toInt(),
                wind.degree.toInt(),
                main.temp.toInt(),
                main.pressure.toInt(),
                main.humidity,
                weather[0].type
            )
        }
    }

    override fun updateCurrentCity(latLan: LatLng) {
        with(SPHelper) {
            setShared(SP_LAT, "${latLan.latitude}")
            setShared(SP_LON, "${latLan.longitude}")
            setShared(SP_LAST_TIME_MINUTES, 0L)
        }
        requestForecast()
    }
}