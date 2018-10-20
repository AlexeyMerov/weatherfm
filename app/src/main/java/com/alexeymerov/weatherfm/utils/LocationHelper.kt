package com.alexeymerov.weatherfm.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper

val locationListener = object : LocationListener {
    override fun onLocationChanged(p0: Location?) {
        debugLog("------------------------ 1")
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        debugLog("------------------------ 2")
    }

    override fun onProviderEnabled(p0: String?) {
        debugLog("------------------------ 3")
    }

    override fun onProviderDisabled(p0: String?) {
        debugLog("------------------------ 4")
    }
}

/**
 * Have to request permissions before
 */
@SuppressLint("MissingPermission")
inline fun Activity.getLocation(f: (Location) -> Unit) {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, Looper.getMainLooper())
    var location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    if (location == null) location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    if (location != null) f.invoke(location)
}