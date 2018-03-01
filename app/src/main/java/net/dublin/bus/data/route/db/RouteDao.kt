package net.dublin.bus.data.route.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes")
    fun getRoutes(): List<Route>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllRoutes(stops: List<Route>)

    @Query("DELETE FROM routes")
    fun clear()
}