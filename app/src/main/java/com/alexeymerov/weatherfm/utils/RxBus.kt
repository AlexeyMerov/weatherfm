package com.alexeymerov.weatherfm.utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object RxBus {

    private val publisher = PublishSubject.create<CustomEvent>()

    fun publish(event: CustomEvent) {
        publisher.onNext(event)
    }

    // Listen should return an Observable and not the publisher
    // Using ofType we filter only events that match that class type
    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)

}

sealed class CustomEvent
object OnLoadingEvent : CustomEvent()