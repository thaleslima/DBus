package net.dublin.bus.data.stop.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import net.dublin.bus.model.Stop

@Dao
interface StopDao {

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT stopnumber, description, routes FROM stops")
    fun getStops(): LiveData<List<Stop>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT stopnumber, description, routes FROM stops WHERE stopnumber LIKE :search || '%' OR description LIKE :search || '%'")
    fun getStopsByText(search: String): List<Stop>

    @Query("SELECT stopnumber, description, latitude, longitude, routes FROM stops ORDER BY abs(latitude - (:latitude)) + abs(longitude - (:longitude)) LIMIT 30")
    fun getStopsByLatLng(latitude: Double, longitude: Double): List<Stop>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllStops(stops: List<Stop>)

    @Query("DELETE FROM stops")
    fun clear()

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT stopnumber, description FROM stops WHERE stopnumber = :stopNumber")
    fun getStopsByNumber(stopNumber: String): Stop

    @Transaction
    fun replaceAll(stops: List<Stop>) {
        clear()
        saveAllStops(stops)
    }
}