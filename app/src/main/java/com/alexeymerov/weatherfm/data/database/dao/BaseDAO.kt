package com.alexeymerov.weatherfm.data.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDAO<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(item: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(items: List<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(items: List<T>)

    @Delete
    fun delete(item: T)

    @Delete
    fun delete(items: List<T>)

}