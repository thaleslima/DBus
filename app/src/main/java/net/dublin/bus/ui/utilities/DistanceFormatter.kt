package net.dublin.bus.ui.utilities

import java.text.DecimalFormat

object DistanceFormatter {
    private const val METRIC_M = "m"
    private const val METRIC_KM = "km"
    private const val METRIC_MILES = "mi"
    private const val METRIC_YARDS = "yd"

    fun formatDistanceKilometer(km: Double): String {
        return formatDistance(km, true)
    }

    fun formatDistanceMiles(miles: Double): String {
        return formatDistance(miles, false)
    }

    private fun formatDistance(distance: Double, isDistanceInKm: Boolean): String {
        val unit: String
        val formattedDistance: String

        if (isDistanceInKm) {
            if (distance < 1) {
                unit = METRIC_M
                formattedDistance = DecimalFormat("#").format(DistanceMetricsHelper.convertKmToM(distance))
            } else {
                unit = METRIC_KM
                formattedDistance = DecimalFormat("#.##").format(distance)
            }
        } else {
            if (distance < 1) {
                unit = METRIC_YARDS
                formattedDistance = DecimalFormat("#").format(DistanceMetricsHelper.convertMilesToYard(distance))
            } else {
                unit = METRIC_MILES
                formattedDistance = DecimalFormat("#.##").format(distance)
            }
        }

        return "$formattedDistance $unit"
    }
}