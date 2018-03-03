package net.dublin.bus.data

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.dublin.bus.data.route.db.RouteDao
import net.dublin.bus.data.stop.db.StopDao
import net.dublin.bus.data.stop.db.StopFavouriteDao
import net.dublin.bus.model.Favourite
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop

@Database(entities = [(Stop::class), (Route::class), (Favourite::class)], version = 1)
abstract class BusDatabase : RoomDatabase() {

    abstract fun getStopDao(): StopDao
    abstract fun getRouteDao(): RouteDao
    abstract fun getFavoriteDao(): StopFavouriteDao

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
            Repository(context).initRepository()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ })
        }
    }
}