package net.dublin.bus.data.stop.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import net.dublin.bus.model.Stop

@Dao
interface StopDao {
    @Query("SELECT stopnumber, description FROM stops")
    fun getStops(): List<Stop>

    @Query("SELECT stopnumber, description, latitude, longitude FROM stops ORDER BY abs(latitude - (:arg0)) + abs(longitude - (:arg1)) LIMIT 30")
    fun getStopsByLatLng(latitude: Double, longitude: Double): List<Stop>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllStops(stops: List<Stop>)

    @Query("DELETE FROM stops")
    fun clear()
}