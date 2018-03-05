package net.dublin.bus.ui.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import net.dublin.bus.common.PreferencesUtils

object PermissionsUtils {
    private fun hasPermission(activity: Activity, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(activity: Activity, requestCode: Int, vararg permissions: String) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    private fun requestPermissions(fragment: Fragment, requestCode: Int, vararg permissions: String) {
        fragment.requestPermissions(permissions, requestCode)
    }

    /**
     * ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION
     */
    object Location {
        private const val LOCATION_ASKED_BEFORE = "location_asked_before"

        fun hasPermission(context: Context): Boolean {
            return (PermissionsUtils.hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    && PermissionsUtils.hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
        }

        fun showRequestPermission(fragment: Fragment, requestCod: Int) {
            if (shouldShowRequestPermissionRationale(fragment.activity)) {
                requestPermissions(fragment, requestCod,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        fun showRequestPermission(activity: Activity, requestCod: Int) {
            if (shouldShowRequestPermissionRationale(activity)) {
                requestPermissions(activity, requestCod,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        fun saveAskedBefore(context: Context) {
            PreferencesUtils.saveData(LOCATION_ASKED_BEFORE, 1, context)
        }

        private fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
            val contactsAskedBefore = PreferencesUtils.getIntData(LOCATION_ASKED_BEFORE, activity)
            val shouldShowRequest = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)

            return contactsAskedBefore == 1 && shouldShowRequest || contactsAskedBefore == 0 && !shouldShowRequest || shouldShowRequest
        }
    }
}
