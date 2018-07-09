package net.dublin.bus.data.recent.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import net.dublin.bus.model.Recent

@Dao
interface RecentDao {
    @Query("SELECT * FROM recent ORDER BY date desc LIMIT 10")
    fun getRecent(): List<Recent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRecent(recent: Recent)
}