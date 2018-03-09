package net.dublin.bus.ui.utilities

import android.content.Context
import android.net.ConnectivityManager
import java.text.DecimalFormat


object Utility {

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected && info.isAvailable
    }


    fun formatValue(value: Double): String {
        var value = value
        val power: Int
        val suffix = " kmbt"
        var formattedNumber = ""

        val formatter = DecimalFormat("#,###.#")
        power = StrictMath.log10(value).toInt()
        value /= Math.pow(10.0, (power / 3 * 3).toDouble())
        formattedNumber = formatter.format(value)
        formattedNumber += suffix[power / 3]
        return if (formattedNumber.length > 4) formattedNumber.replace("\\.[0-9]+".toRegex(), "") else formattedNumber
    }
}