package com.alexeymerov.weatherfm.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexeymerov.weatherfm.data.database.dao.ForecastDAO
import com.alexeymerov.weatherfm.data.database.entity.ForecastEntity

@Database(entities = [ForecastEntity::class], version = 1, exportSchema = false)
abstract class WeatherFmDatabase : RoomDatabase() {

    abstract fun forecastDao(): ForecastDAO

}