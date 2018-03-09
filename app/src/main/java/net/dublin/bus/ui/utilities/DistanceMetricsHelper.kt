package net.dublin.bus.ui.utilities

object DistanceMetricsHelper {

    fun convertKmToMiles(km: Double): Double {
        return km * 0.621371
    }

    fun convertKmToM(km: Double): Double {
        return km * 1000
    }

    fun convertMilesToKm(miles: Double): Double {
        return miles * 1.60934
    }

    fun convertMilesToYard(miles: Double): Double {
        return miles * 1760
    }
}