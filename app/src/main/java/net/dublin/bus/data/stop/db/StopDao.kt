package net.dublin.bus.data.stop.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import net.dublin.bus.model.Stop

@Dao
interface StopDao {
    @Query("SELECT stopnumber, description, routes FROM stops")
    fun getStops(): LiveData<List<Stop>>

    @Query("SELECT stopnumber, description, routes FROM stops WHERE stopnumber LIKE :search || '%' OR description LIKE :search || '%'")
    fun getStopsByText(search: String): List<Stop>

    @Query("SELECT stopnumber, description, latitude, longitude, routes FROM stops ORDER BY abs(latitude - (:latitude)) + abs(longitude - (:longitude)) LIMIT 30")
    fun getStopsByLatLng(latitude: Double, longitude: Double): List<Stop>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllStops(stops: List<Stop>)

    @Query("DELETE FROM stops")
    fun clear()

    @Query("SELECT stopnumber, description FROM stops WHERE stopnumber = :stopNumber")
    fun getStopsByNumber(stopNumber: String): Stop
}