package com.alexeymerov.weatherfm

import android.app.Application
import com.alexeymerov.weatherfm.koin.repositoryModule
import com.alexeymerov.weatherfm.koin.viewModelModule
import com.alexeymerov.weatherfm.utils.SPHelper
import com.facebook.stetho.Stetho
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        SPHelper.init(this, "weatherfm")
        startKoin(this, listOf(viewModelModule, repositoryModule))
    }
}