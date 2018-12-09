package net.dublin.bus.data.stop.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.dublin.bus.model.Favourite

@Dao
interface StopFavouriteDao {
    @Query("SELECT stopnumber, description FROM favourites")
    fun getStops(): LiveData<List<Favourite>>

    @Query("SELECT stopnumber, description FROM favourites")
    fun getQtdStops(): List<Favourite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(favourite: Favourite)

    @Query("DELETE FROM favourites")
    fun clear()

    @Query("SELECT COUNT(stopNumber) FROM favourites WHERE stopnumber = :stopNumber")
    fun isFavourite(stopNumber: String): Int

    @Query("DELETE FROM favourites WHERE stopnumber = :stopNumber")
    fun removeFavourite(stopNumber: String)
}