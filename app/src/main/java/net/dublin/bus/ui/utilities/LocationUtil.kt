package net.dublin.bus.ui.utilities

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.support.v4.app.Fragment

object LocationUtil {
    fun requestLocationOrShowMessage(fragment: Fragment, requestCode: Int): Boolean {
        if (!PermissionsUtils.Location.hasPermission(fragment.activity)) {
            PermissionsUtils.Location.showRequestPermission(fragment, requestCode)
            return false
        }
        if (!locationIsActivated(fragment.activity)) {
            DialogUtil.showAlertLocationDialog(fragment.activity)
            return false
        }

        return true
    }

    fun requestLocationOrShowMessage(activity: Activity, requestCode: Int): Boolean {
        if (!PermissionsUtils.Location.hasPermission(activity)) {
            PermissionsUtils.Location.showRequestPermission(activity, requestCode)
            return false
        }
        if (!locationIsActivated(activity)) {
            DialogUtil.showAlertLocationDialog(activity)
            return false
        }
        return true
    }

    private fun locationIsActivated(context: Context): Boolean {
        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
