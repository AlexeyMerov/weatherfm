package com.alexeymerov.weatherfm.viewmodel

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import com.alexeymerov.weatherfm.App
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected fun <T> singleTransformer(): SingleTransformer<T, T> = SingleTransformer {
        it
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    fun getContext() = getApplication<App>()

    fun getString(@StringRes id: Int): String = getContext().getString(id)

}