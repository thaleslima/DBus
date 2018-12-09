package net.dublin.bus.data

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import net.dublin.bus.BuildConfig
import net.dublin.bus.data.recent.db.RecentDao
import net.dublin.bus.data.route.db.RouteDao
import net.dublin.bus.data.route.db.RouteStopDao
import net.dublin.bus.data.stop.db.StopDao
import net.dublin.bus.data.stop.db.StopFavouriteDao
import net.dublin.bus.model.*

@Database(entities = [
    (Stop::class),
    (Route::class),
    (Favourite::class),
    (Recent::class),
    (RouteStop::class)], version = 1, exportSchema = false)
abstract class BusDatabase : RoomDatabase() {

    abstract fun getStopDao(): StopDao
    abstract fun getRouteDao(): RouteDao
    abstract fun getFavoriteDao(): StopFavouriteDao
    abstract fun getRecentDao(): RecentDao
    abstract fun getRouteStopDao(): RouteStopDao

    companion object {
        private var INSTANCE: BusDatabase? = null

        fun getDatabase(context: Context): BusDatabase {
            if (INSTANCE == null) {
                synchronized(BusDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder<BusDatabase>(context.applicationContext,
                                BusDatabase::class.java, "bus_database")
                                .fallbackToDestructiveMigration()
                                .addCallback(DatabaseCallback(context))
                                .build()
                    }
                }
            }

            return INSTANCE!!
        }
    }

    private class DatabaseCallback(val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val repository = Repository(context)

            repository.initRepository()

            if (!BuildConfig.MOCK) {
                repository.startFetchService()
            }
        }
    }
}