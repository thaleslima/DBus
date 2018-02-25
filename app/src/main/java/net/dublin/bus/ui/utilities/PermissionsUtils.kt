package net.dublin.bus.ui.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment

import net.dublin.bus.R

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
        fun hasPermission(context: Context): Boolean {
            return (PermissionsUtils.hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    && PermissionsUtils.hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION))
        }

        private fun openMessage(context: Context) {
            DialogUtil.showDialogPermission(context,
                    context.getString(R.string.permission_title),
                    context.getString(R.string.permission_message))
        }

        internal fun requestPermission(activity: Activity, requestCode: Int) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {

                requestPermissions(activity, requestCode, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                openMessage(activity)
            }
        }

        internal fun requestPermission(fragment: Fragment, requestCode: Int) {
            if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(fragment, requestCode,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                openMessage(fragment.activity)
            }
        }
    }
}
