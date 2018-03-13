package net.dublin.bus.ui.view.utilities

import android.content.Context

object StringUtil {
    fun getStringFromFile(context: Context, filePath: String): String {
        return try {
            val stream = context.resources.assets.open(filePath)
            val inputAsString = stream.bufferedReader().use { it.readText() }  // defaults to UTF-8
            stream.close()
            inputAsString
        } catch (ex: Exception) {
            ""
        }
    }
}
