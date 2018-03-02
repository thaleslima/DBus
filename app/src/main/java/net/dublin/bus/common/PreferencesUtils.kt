package net.dublin.bus.common

import android.content.Context
import android.preference.PreferenceManager

object PreferencesUtils {
    private const val PREFERENCES_MAP = "preferences_map"


    fun getIntData(name: String, context: Context): Int {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        return settings.getInt(name, 0)
    }

    fun saveData(name: String, value: Int, context: Context) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = settings.edit()
        editor.putInt(name, value)
        editor.apply()
    }

    fun saveShowMapAtRouteDeail(context: Context, value: Boolean) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = settings.edit()
        editor.putBoolean(PREFERENCES_MAP, value)
        editor.apply()
    }

    fun getShowMapAtRouteDeail(context: Context): Boolean {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        return settings.getBoolean(PREFERENCES_MAP, false)
    }
}
