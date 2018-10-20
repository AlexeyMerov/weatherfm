package com.alexeymerov.weatherfm.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.alexeymerov.weatherfm.data.database.entity.ForecastEntity

@Dao
interface ForecastDAO : BaseDAO<ForecastEntity> {

    companion object {
        const val TABLE_NAME: String = "forecast_entity"
        const val DATE_ROW: String = "dateMillis"
    }

    @Query("SELECT * FROM $TABLE_NAME ORDER BY $DATE_ROW ASC")
    fun getAll(): List<ForecastEntity>

    @Query("SELECT * FROM $TABLE_NAME ORDER BY $DATE_ROW ASC")
    fun getAllLive(): LiveData<List<ForecastEntity>>

    @Query("DELETE FROM $TABLE_NAME")
    fun removeAll()


}