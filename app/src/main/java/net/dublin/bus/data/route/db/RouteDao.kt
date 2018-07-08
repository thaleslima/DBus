package net.dublin.bus.data.route.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import net.dublin.bus.model.Route
import android.arch.persistence.room.Transaction

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes")
    fun getRoutes(): LiveData<List<Route>>

    @Query("SELECT * FROM routes WHERE number LIKE :search || '%'")
    fun getStopsByText(search: String): List<Route>

    @Query("SELECT * FROM routes WHERE number = :number")
    fun getRouteByNumber(number: String): Route?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllRoutes(stops: List<Route>)

    @Query("DELETE FROM routes")
    fun clear()

    @Transaction
    fun replaceAll(stops: List<Route>) {
        clear()
        saveAllRoutes(stops)
    }
}