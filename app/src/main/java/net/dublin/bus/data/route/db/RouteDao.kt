package net.dublin.bus.data.route.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.dublin.bus.model.Route
import androidx.room.Transaction

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