package com.alexeymerov.weatherfm.data.server.communicator

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

abstract class BaseCommunicator<T>(interfaceClass: Class<T>, apiUrl: String) {

    protected var apiService: T

    init {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(5, 30, TimeUnit.SECONDS))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

//        if (BuildConfig.DEBUG) {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder
            .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(StethoInterceptor())
//        }

        val retrofitBuilder = Retrofit.Builder()
            .client(okHttpClientBuilder.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        val retrofit = retrofitBuilder.baseUrl(apiUrl).build()
        apiService = retrofit.create<T>(interfaceClass)
    }

}