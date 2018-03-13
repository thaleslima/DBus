package net.dublin.bus.common

/**
 * Jason Winn
 * http://jasonwinn.org
 * Created July 10, 2013
 *
 * Description: Small class that provides approximate distance between
 * two points using the Haversine formula.
 *
 * Call in a static context:
 * Haversine.distance(47.6788206, -122.3271205,
 * 47.6788206, -122.5271205)
 * --> 14.973190481586224 [km]
 *
 */

object Haversine {
    private val EARTH_RADIUS = 6371 // Approx Earth radius in KM

    fun distance(startLat: Double, startLong: Double,
                 endLat: Double, endLong: Double): Double {
        var startLat = startLat
        var endLat = endLat

        val dLat = Math.toRadians(endLat - startLat)
        val dLong = Math.toRadians(endLong - startLong)

        startLat = Math.toRadians(startLat)
        endLat = Math.toRadians(endLat)

        val a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return EARTH_RADIUS * c // <-- d
    }

    fun haversin(`val`: Double): Double {
        return Math.pow(Math.sin(`val` / 2), 2.0)
    }
}