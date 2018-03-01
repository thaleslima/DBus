package net.dublin.bus.common

import android.content.Context
import android.preference.PreferenceManager

object PreferencesUtils {
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
}
