package com.alexeymerov.weatherfm.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexeymerov.weatherfm.data.database.dao.ForecastDAO

@Entity(tableName = ForecastDAO.TABLE_NAME)
data class ForecastEntity(
    @PrimaryKey
    val dateMillis: Long,
    val dateDay: Int,
    val dateMonth: Int,
    val cloudsPercent: Int,
    val windSpeed: Int,
    val windDegree: Int,
    val temp: Int,
    val pressure: Int,
    val humidity: Int,
    val weatherType: String
)