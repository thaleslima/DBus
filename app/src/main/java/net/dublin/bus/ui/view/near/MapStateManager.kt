package net.dublin.bus.ui.view.near

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

class MapStateManager(
        var restored: Boolean = false,
        var locationCurrent: LatLng? = null,
        var locationSearch: LatLng? = null,
        var stopNumberSelected: String? = null) {

    companion object {
        private const val LATITUDE_CURRENT = "latitude_current"
        private const val LONGITUDE_CURRENT = "longitude_current"

        private const val LATITUDE_SEARCH = "latitude_search"
        private const val LONGITUDE_SEARCH = "longitude_search"

        private const val BUNDLE_MARKER_STOP = "bundle_marker_stop"
    }

    fun saveLocationCurrent(savedInstanceState: Bundle?, map: GoogleMap?) {
        map?.cameraPosition?.target?.let {
            savedInstanceState?.putDouble(LATITUDE_CURRENT, it.latitude)
            savedInstanceState?.putDouble(LONGITUDE_CURRENT, it.longitude)
        }
    }

    fun saveLocationSearch(savedInstanceState: Bundle?, latitude: Double, longitude: Double) {
        savedInstanceState?.putDouble(LATITUDE_SEARCH, latitude)
        savedInstanceState?.putDouble(LONGITUDE_SEARCH, longitude)
    }

    fun saveStopNumberSelected(savedInstanceState: Bundle?, stopNumber: String?) {
        savedInstanceState?.putString(BUNDLE_MARKER_STOP, stopNumber)
    }

    fun restoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return

        var latitude = savedInstanceState.getDouble(LATITUDE_CURRENT)
        var longitude = savedInstanceState.getDouble(LONGITUDE_CURRENT)
        locationCurrent = LatLng(latitude, longitude)

        latitude = savedInstanceState.getDouble(LATITUDE_SEARCH)
        longitude = savedInstanceState.getDouble(LONGITUDE_SEARCH)
        locationSearch = LatLng(latitude, longitude)

        stopNumberSelected = savedInstanceState.getString(BUNDLE_MARKER_STOP)
        restored = locationCurrent != null && locationSearch != null && stopNumberSelected != null
    }
}
