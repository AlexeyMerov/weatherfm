package com.alexeymerov.weatherfm.utils.extensions

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}

inline fun postDelayedMain(millis: Int, crossinline f: () -> Unit) = postDelayedMain(millis.toLong()) { f.invoke() }
inline fun postDelayedMain(millis: Long, crossinline f: () -> Unit) = postDelayed(millis, true) { f.invoke() }
inline fun postDelayed(millis: Long, onMainThread: Boolean = false, crossinline f: () -> Unit) {
    val handler = if (onMainThread) Handler(Looper.getMainLooper()) else Handler()
    handler.postDelayed({ f.invoke() }, millis)
}

interface AutoUpdatableAdapter<in T> {
    fun RecyclerView.Adapter<*>.autoNotify(oldList: List<T>, newList: List<T>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPosition: Int, newPosition: Int) =
                compareItems(oldList[oldPosition], newList[newPosition])

            override fun areContentsTheSame(oldPosition: Int, newPosition: Int) =
                oldList[oldPosition] == newList[newPosition]

            override fun getOldListSize() = oldList.size
            override fun getNewListSize() = newList.size
            override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? =
                compareContent(oldList[oldPosition], newList[newPosition])
        }).dispatchUpdatesTo(this)
    }

    fun compareItems(old: T, new: T): Boolean

    fun compareContent(old: T, new: T): Any? = null
}

inline fun doOnRxAsync(millis: Long = 0, crossinline backgroundFun: () -> Unit) {
    Completable.create { backgroundFun.invoke() }
        .delay(millis, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .doOnError { t -> t.printStackTrace() }
        .subscribe()
}

//Todo make disposable management
@SuppressLint("CheckResult")
inline fun doOnRxAsync(millis: Long = 0, crossinline backgroundFun: () -> Unit, crossinline mainThreadFun: () -> Unit) {
    Completable.create { backgroundFun.invoke() }
        .delay(millis, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError { t -> t.printStackTrace() }
        .subscribe { mainThreadFun.invoke() }
}

//inline fun <reified T> Gson.fromJsonCustom(json: String): T = fromJson<T>(json, object : TypeToken<T>() {}.type)
//inline fun <reified T> Gson.toJsonCustom(json: String): String = toJson(json, object : TypeToken<T>() {}.type)

infix fun <T> T.or(any: T) = if (Random().nextBoolean()) this else any
fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()
fun randomBoolean() = Random().nextBoolean()
fun isMarshmallowAndAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun currentMillis() = System.currentTimeMillis()
fun currentMinutes() = currentMillis() / 1000 / 60
