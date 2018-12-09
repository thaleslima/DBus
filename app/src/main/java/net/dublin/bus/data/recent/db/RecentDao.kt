package net.dublin.bus.data.recent.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.dublin.bus.model.Recent

@Dao
interface RecentDao {
    @Query("SELECT * FROM recent ORDER BY date desc LIMIT 10")
    fun getRecent(): List<Recent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRecent(recent: Recent)
}