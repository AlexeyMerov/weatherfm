package com.alexeymerov.weatherfm.presentation.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alexeymerov.weatherfm.R
import com.alexeymerov.weatherfm.utils.createAlert
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
abstract class PermissionActivity : BaseActivity() {

    companion object {
        private const val LOCATION_REQUEST_CODE = 123
        private const val RATIONALE_REQUEST_CODE = 124
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var onLocationListener: (Location) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    protected fun requestLocation(checkOpenSettings: Boolean = false, f: (Location?) -> Unit) {
        onLocationListener = f
        val permissionGranted = checkIsGranted(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionGranted) fusedLocationClient.lastLocation.addOnSuccessListener(f::invoke)
        else requestPermission(checkOpenSettings)
    }

    private fun checkIsGranted(vararg permissions: String): Boolean {
        permissions.forEach {
            val isGranted = checkIsGranted(it)
            if (!isGranted) return false
        }
        return true
    }

    private fun checkIsGranted(permission: String) = ContextCompat.checkSelfPermission(
        this, permission
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(checkOpenSettings: Boolean = false) {
        val needOpenSettings = !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
        when {
            needOpenSettings && checkOpenSettings -> {
                createAlert(R.string.request_location_title, R.string.request_location_description) {
                    openSettingScreen()
                }
            }
            else -> ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    private fun openSettingScreen() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, RATIONALE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                fusedLocationClient.lastLocation.addOnSuccessListener(onLocationListener::invoke)
            }
        }
    }
}