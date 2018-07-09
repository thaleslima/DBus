package net.dublin.bus.data.stop.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import io.reactivex.Observable
import io.reactivex.Single
import net.dublin.bus.data.stop.local.LocalStopDataSource
import net.dublin.bus.data.stop.local.LocalStopFavouriteDataSource
import net.dublin.bus.data.stop.remote.RemoteStopDataSource
import net.dublin.bus.model.Favourite
import net.dublin.bus.model.Stop

class StopRepository(context: Context) {
    private var localSource: LocalStopDataSource = LocalStopDataSource(context)
    private var remoteSource: RemoteStopDataSource = RemoteStopDataSource()
    private var localFavouriteSource: LocalStopFavouriteDataSource = LocalStopFavouriteDataSource(context)

    fun getData(): LiveData<List<Stop>> {
        return localSource.getAll()
    }

    fun getStopsByLatLng(latitude: Double, longitude: Double): Single<MutableList<Stop>> {
        return localSource
                .getStopsByLatLng(latitude, longitude)
                .flatMapIterable { it }
                .map { it1 ->
                    it1.calculateDistance(latitude, longitude)
                    it1
                }
                .toSortedList { first, second -> first.distance.compareTo(second.distance) }
    }

    fun isFavourite(stopNumber: String): Observable<Boolean> {
        return localFavouriteSource.isFavourite(stopNumber)
    }

    fun saveFavourite(favourite: Favourite): Observable<Boolean> {
        return localFavouriteSource.save(favourite)
    }

    fun removeFavourite(stopNumber: String): Observable<Boolean> {
        return localFavouriteSource.removeFavourite(stopNumber)
    }

    fun getFavourites(): LiveData<List<Favourite>> {
        return localFavouriteSource.getAll()
    }

    fun getStopsByText(search: String): Observable<List<Stop>> {
        return localSource.getStopsByText(search)
    }

    fun getStopsByNumber(stopNumber: String): Observable<Stop> {
        return localSource.getStopsByNumber(stopNumber)
    }

    fun removeAllFavourites() {
        return localFavouriteSource.removeAllFavourites()
    }

    fun getQtdStops(): Observable<Int> {
        return localFavouriteSource.getQtdStops()
    }
}
