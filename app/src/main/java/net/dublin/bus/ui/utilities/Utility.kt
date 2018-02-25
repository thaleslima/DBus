package net.dublin.bus.ui.utilities

import android.content.Context
import android.net.ConnectivityManager

object Utility {

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected && info.isAvailable
    }
}