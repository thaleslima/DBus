package net.dublin.bus.ui.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.util.Log

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class LocationRequestWrapper(private val activity: Activity, private val onNewLocationListener: OnNewLocationListener?) : LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null

    init {
        setupGoogleApiClient(activity)
        setupLocationRequest()
    }

    fun connect() {
        googleApiClient!!.connect()
    }

    fun disconnect() {
        if (googleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient!!.disconnect()
        }
    }

    fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        }
    }

    private fun setupGoogleApiClient(context: Context) {
        googleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_REQUEST_INTERVAL.toLong())
                .setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL.toLong())
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
        } else {
            onNewLocationListener?.onNewLocation(location)
        }
    }

    override fun onConnectionSuspended(i: Int) {
        Log.d(TAG, "Location service")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }

        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.errorCode)
        }
    }

    override fun onLocationChanged(location: Location) {
        //if (onNewLocationListener != null) {
        //onNewLocationListener.onNewLocation(location);
        //}
    }

    interface OnNewLocationListener {
        fun onNewLocation(location: Location)
    }

    companion object {
        private val TAG = LocationRequestWrapper::class.java.simpleName
        private const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
        private const val LOCATION_REQUEST_INTERVAL = 10000 // 10 seconds, in milliseconds
        private const val LOCATION_REQUEST_FASTEST_INTERVAL = 1000 // 1 second, in milliseconds
    }

}