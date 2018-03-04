package net.dublin.bus.model

import java.text.SimpleDateFormat
import java.util.*

data class StopData(
        var lineNote: String? = null,
        var expectedDepartureTime: String? = null,
        var destinationName: String? = null,
        var inCongestion: String? = null,
        var monitored: String? = null,
        var publishedLineName: String? = null,
        var responseTimestamp: String? = null) {

    fun timeRemainingFormatted(): String {
        var time = ""
        try {
            val localDate1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(responseTimestamp?.substring(0, 19)?.replace("T", " "))
            val localDate2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(expectedDepartureTime?.substring(0, 19)?.replace("T", " "))

            if (!(localDate1 == null || localDate2 == null)) {
                val i = ((localDate2.time - localDate1.time) / 1000 % 3600 / 60)
                time = if (i < 1) {
                    "Due"
                } else {
                    Integer.toString(i.toInt()) + " min"
                }
            }

        } catch (exception: Throwable) {
            exception.printStackTrace()
        }
        return time
    }
}