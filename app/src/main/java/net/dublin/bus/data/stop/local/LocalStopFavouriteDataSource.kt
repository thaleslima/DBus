package net.dublin.bus.data.stop.local

import android.arch.lifecycle.LiveData
import android.content.Context
import io.reactivex.Observable
import net.dublin.bus.data.BusDatabase
import net.dublin.bus.data.stop.db.StopFavouriteDao
import net.dublin.bus.model.Favourite
import java.io.IOException

class LocalStopFavouriteDataSource(context: Context) {
    private var dao: StopFavouriteDao

    init {
        val db = BusDatabase.getDatabase(context)
        dao = db.getFavoriteDao()
    }

    fun getAll(): LiveData<List<Favourite>> {
        return dao.getStops()
    }

    fun getQtdStops(): Observable<Int> {
        return Observable.fromCallable { dao.getQtdStops() }.map { it.size }
    }

    fun isFavourite(stopNumber: String): Observable<Boolean> {
        return Observable.create { subscriber ->
            try {
                subscriber.onNext(dao.isFavourite(stopNumber) > 0)
                subscriber.onComplete()
            } catch (e: IOException) {
                e.localizedMessage
                if (!subscriber.isDisposed) {
                    subscriber.onError(e)
                }
            }
        }
    }

    fun save(favourite: Favourite): Observable<Boolean> {
        return Observable.create { subscriber ->
            try {
                dao.save(favourite)
                subscriber.onNext(true)
                subscriber.onComplete()
            } catch (e: IOException) {
                e.localizedMessage
                if (!subscriber.isDisposed) {
                    subscriber.onError(e)
                }
            }
        }
    }

    fun removeFavourite(stopNumber: String): Observable<Boolean> {
        return Observable.create { subscriber ->
            try {
                dao.removeFavourite(stopNumber)
                subscriber.onNext(true)
                subscriber.onComplete()
            } catch (e: IOException) {
                e.localizedMessage
                if (!subscriber.isDisposed) {
                    subscriber.onError(e)
                }
            }
        }
    }

    fun removeAllFavourites() {
        dao.clear()
    }
}