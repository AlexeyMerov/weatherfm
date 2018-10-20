package com.alexeymerov.weatherfm.utils

import android.util.Log
import com.alexeymerov.weatherfm.BuildConfig

const val TAG = "------------- ReditTestTask"

fun Any.toLog(tag: String = TAG) = debugLog(this, tag)

fun infoLog(any: Any?, tag: String = TAG) = whenDebug { Log.i(tag, checkNotNull(any)) }

internal inline fun errorLog(any: Any?, tag: String = TAG, crossinline onDone: () -> Unit = {}) {
    whenDebug { Log.e(tag, checkNotNull(any)) }
    onDone.invoke()
}

fun errorLog(exception: Exception, tag: String = TAG) = whenDebug { Log.e(tag, checkNotNull(exception.message)) }

fun errorLog(any: Any?, tag: String = TAG, tr: Throwable) = whenDebug { Log.e(tag, checkNotNull(any), tr) }

fun debugLog(any: Any?, tag: String = TAG) = whenDebug { Log.d(tag, checkNotNull(any)) }

fun debugLog(any: Any?, tag: String = TAG, tr: Throwable) = whenDebug { Log.d(tag, checkNotNull(any), tr) }

fun verboseLog(any: Any?, tag: String = TAG) = whenDebug { Log.v(tag, checkNotNull(any)) }

fun warnLog(any: Any?, tag: String = TAG) = whenDebug { Log.w(tag, checkNotNull(any)) }

private inline fun whenDebug(crossinline f: () -> Unit) {
    if (BuildConfig.DEBUG) f.invoke()
}

private fun checkNotNull(any: Any?): String = if (any?.toString() != null) any.toString() else "string for log is null"