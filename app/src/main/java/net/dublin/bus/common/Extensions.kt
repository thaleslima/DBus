package net.dublin.bus.common

import android.content.Context
import java.io.IOException

fun Context.readStringFromFile(nameFile: String): String? {
    var contactsString: String? = null
    try {
        val inputStream = assets.open(nameFile)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()

        contactsString = String(buffer)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return contactsString
}