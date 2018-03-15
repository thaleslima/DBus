package net.dublin.bus.data.routestop.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import net.dublin.bus.model.Route
import net.dublin.bus.model.RouteStop
import net.dublin.bus.model.Stop

@Dao
interface RouteStopDao {
    @Query("SELECT * FROM stops INNER JOIN route_stop ON stops.stopNumber=route_stop.numberStop WHERE route_stop.numberRoute IN (:numberRoute) ORDER BY stops.stopNumber")
    fun getStopsForRoutes(numberRoute: Array<String>): List<Stop>

    @Query("SELECT * FROM routes INNER JOIN route_stop ON routes.number=route_stop.numberRoute WHERE route_stop.numberStop = :numberStop ORDER BY routes.number")
    fun getRoutesForStop(numberStop: String): List<Route>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllRouteStops(stops: List<RouteStop>)

    @Query("DELETE FROM route_stop")
    fun clear()
}