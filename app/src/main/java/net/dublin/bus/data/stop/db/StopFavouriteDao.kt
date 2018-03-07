package net.dublin.bus.data.stop.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import net.dublin.bus.model.Favourite

@Dao
interface StopFavouriteDao {
    @Query("SELECT stopnumber, description FROM favourites")
    fun getStops(): List<Favourite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(favourite: Favourite)

    @Query("DELETE FROM favourites")
    fun clear()

    @Query("SELECT COUNT(stopNumber) FROM favourites WHERE stopnumber = :arg0")
    fun isFavourite(stopNumber: String): Int

    @Query("DELETE FROM favourites WHERE stopnumber = :arg0")
    fun removeFavourite(stopNumber: String)
}