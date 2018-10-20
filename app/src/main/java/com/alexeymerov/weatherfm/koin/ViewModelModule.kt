package com.alexeymerov.weatherfm.koin

import com.alexeymerov.weatherfm.viewmodel.ForecastViewModel
import com.alexeymerov.weatherfm.viewmodel.contract.IForecastViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

val viewModelModule: Module = module {
    viewModel { ForecastViewModel(get(), get()) as IForecastViewModel }
}
