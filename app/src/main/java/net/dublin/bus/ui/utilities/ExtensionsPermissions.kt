package net.dublin.bus.ui.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

private fun hasPermission(context: Context, permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

private fun requestPermissions(activity: Activity, requestCode: Int, vararg permissions: String) {
    ActivityCompat.requestPermissions(activity, permissions, requestCode)
}

private fun requestPermissions(fragment: Fragment, requestCode: Int, vararg permissions: String) {
    fragment.requestPermissions(permissions, requestCode)
}

private fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
    val shouldShowRequest = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
    return  shouldShowRequest || !shouldShowRequest || shouldShowRequest
}

private fun showLocationRequestPermission(fragment: Fragment, requestCod: Int) {
    if (shouldShowRequestPermissionRationale(fragment.requireActivity())) {
        requestPermissions(fragment, requestCod,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

private fun showLocationRequestPermission(activity: Activity, requestCod: Int) {
    if (shouldShowRequestPermissionRationale(activity)) {
        requestPermissions(activity, requestCod,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }
}


fun Context.hasLocationPermission(): Boolean {
    return (hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            && hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
}

fun androidx.fragment.app.Fragment.hasLocationPermission(): Boolean {
    return requireContext().hasLocationPermission()
}

fun androidx.fragment.app.Fragment.requestLocationOrShowMessage(requestCode: Int): Boolean {
    if (!hasLocationPermission()) {
        showLocationRequestPermission(this, requestCode)
        return false
    }
    return true
}

fun Activity.requestLocationOrShowMessage(requestCode: Int): Boolean {
    if (!hasLocationPermission()) {
        showLocationRequestPermission(this, requestCode)
        return false
    }
    return true
}


