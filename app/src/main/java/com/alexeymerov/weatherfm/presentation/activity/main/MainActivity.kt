package com.alexeymerov.weatherfm.presentation.activity.main

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.alexeymerov.weatherfm.PLACE_AUTOCOMPLETE_REQUEST_CODE
import com.alexeymerov.weatherfm.R
import com.alexeymerov.weatherfm.SP_CITY_NAME
import com.alexeymerov.weatherfm.presentation.base.PermissionActivity
import com.alexeymerov.weatherfm.presentation.fragment.ForecastFragment
import com.alexeymerov.weatherfm.utils.*
import com.alexeymerov.weatherfm.utils.extensions.isGooglePlayServicesAvailable
import com.alexeymerov.weatherfm.viewmodel.contract.IForecastViewModel
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_no_internet_panel.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : PermissionActivity() {

    private val viewModel by viewModel<IForecastViewModel>()
    private var searchMenuItem: MenuItem? = null
    private var gpsMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setNoIntenetView(noInternetTextView)
        initViews()
    }

    private fun initViews() {
        val lastCityName = SPHelper.getShared(SP_CITY_NAME, "")
        if (lastCityName.isNotEmpty()) setToolbarTitle(lastCityName)
        replaceFragment(ForecastFragment.newInstance())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        searchMenuItem = menu.findItem(R.id.action_search)
        gpsMenuItem = menu.findItem(R.id.action_gps)
        return true
    }

    override fun onInternetStateChanged(isConnected: Boolean) {
        searchMenuItem?.isEnabled = isConnected
        gpsMenuItem?.isEnabled = isConnected
        if (isConnected) {
            searchMenuItem?.setIcon(R.drawable.ic_search_white)
            gpsMenuItem?.setIcon(R.drawable.ic_map_marker)
        } else {
            searchMenuItem?.setIcon(R.drawable.ic_search_white_50)
            gpsMenuItem?.setIcon(R.drawable.ic_map_marker_50)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> onSearchClick()
            R.id.action_gps -> onLocationClick()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onSearchClick() {
        if (!isGooglePlayServicesAvailable()) return
        val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this)
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun onLocationClick() {
        requestLocation(true) {
            when {
                it != null -> handleLocationFound(it)
                else -> createAlert(
                    R.string.location_not_available_title,
                    R.string.location_not_available_description,
                    negativeButtonStringId = 0
                )
            }
        }
    }

    private fun handleLocationFound(location: Location) {
        viewModel.updateCurrentCity(LatLng(location.latitude, location.longitude))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != PLACE_AUTOCOMPLETE_REQUEST_CODE) return
        when (resultCode) {
            Activity.RESULT_OK -> handleCityChosen(data)
            PlaceAutocomplete.RESULT_ERROR -> handleChoseError(data)
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }
    }

    private fun handleChoseError(data: Intent?) {
        val status = PlaceAutocomplete.getStatus(this, data)
        errorLog(status)
    }

    private fun handleCityChosen(data: Intent?) {
        RxBus.publish(OnLoadingEvent)
        PlaceAutocomplete.getPlace(this, data).apply {
            SPHelper.setShared(SP_CITY_NAME, name)
            setToolbarTitle(name)
            viewModel.updateCurrentCity(latLng)
        }
    }
}
