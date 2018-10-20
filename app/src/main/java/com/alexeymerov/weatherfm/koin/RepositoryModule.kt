package com.alexeymerov.weatherfm.koin

import android.content.Context
import androidx.room.Room
import com.alexeymerov.weatherfm.WEATHER_URL
import com.alexeymerov.weatherfm.data.database.WeatherFmDatabase
import com.alexeymerov.weatherfm.data.repository.ForecastRepository
import com.alexeymerov.weatherfm.data.repository.contracts.IForecastRepository
import com.alexeymerov.weatherfm.data.server.communicator.WeatherCommunicator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

val repositoryModule: Module = module {
    single { provideDatabase(androidContext()) }

    single { provideWeatherCommunicator() }

    single { ForecastRepository(get(), get()) as IForecastRepository }
}

//private fun provideSharedPrefsHelper(context: Context) = SPHelper
private fun provideDatabase(context: Context) = Room
    .databaseBuilder(context, WeatherFmDatabase::class.java, "weatherfm-database")
    .build()

private fun provideWeatherCommunicator() = WeatherCommunicator(WEATHER_URL)


