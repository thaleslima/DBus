package net.dublin.bus.data.stop.repository

import androidx.lifecycle.LiveData
import android.content.Context
import io.reactivex.Observable
import io.reactivex.Single
import net.dublin.bus.data.BusDatabase
import net.dublin.bus.data.stop.db.StopDao
import net.dublin.bus.data.stop.db.StopFavouriteDao
import net.dublin.bus.model.Favourite
import net.dublin.bus.model.Stop
import java.io.IOException

class StopRepository(context: Context) {
    private var stopDao: StopDao
    private var favouriteDao: StopFavouriteDao

    init {
        val db = BusDatabase.getDatabase(context)
        stopDao = db.getStopDao()
        favouriteDao = db.getFavoriteDao()
    }

    fun getData(): LiveData<List<Stop>> {
        return stopDao.getStops()
    }

    fun getStopsByLatLng(latitude: Double, longitude: Double): Single<MutableList<Stop>> {
        return Observable.fromCallable { stopDao.getStopsByLatLng(latitude, longitude) }
                .flatMapIterable { it }
                .map { it1 ->
                    it1.calculateDistance(latitude, longitude)
                    it1
                }.toSortedList { first, second -> first.distance.compareTo(second.distance) }
    }

    fun isFavourite(stopNumber: String): Observable<Boolean> {
        return Observable.create { subscriber ->
            try {
                subscriber.onNext(favouriteDao.isFavourite(stopNumber) > 0)
                subscriber.onComplete()
            } catch (e: IOException) {
                e.localizedMessage
                if (!subscriber.isDisposed) {
                    subscriber.onError(e)
                }
            }
        }
    }

    fun saveFavourite(favourite: Favourite): Observable<Boolean> {
        return Observable.create { subscriber ->
            try {
                favouriteDao.save(favourite)
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
                favouriteDao.removeFavourite(stopNumber)
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

    fun getFavourites(): LiveData<List<Favourite>> {
        return favouriteDao.getStops()
    }

    fun getStopsByText(search: String): Observable<List<Stop>> {
        return Observable.create { subscriber ->
            try {
                subscriber.onNext(stopDao.getStopsByText(search))
                subscriber.onComplete()
            } catch (e: IOException) {
                e.localizedMessage
                if (!subscriber.isDisposed) {
                    subscriber.onError(e)
                }
            }
        }
    }

    fun getStopsByNumber(stopNumber: String): Observable<Stop> {
        return Observable.fromCallable { stopDao.getStopsByNumber(stopNumber) }
    }

    fun removeAllFavourites() {
        return favouriteDao.clear()
    }

    fun getQtdStops(): Observable<Int> {
        return Observable.fromCallable { favouriteDao.getQtdStops() }.map { it.size }
    }
}
