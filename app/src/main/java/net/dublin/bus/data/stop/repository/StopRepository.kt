package net.dublin.bus.data.stop.repository

import android.app.Application
import io.reactivex.Observable
import io.reactivex.Single
import net.dublin.bus.data.stop.local.LocalStopDataSource
import net.dublin.bus.data.stop.local.LocalStopFavouriteDataSource
import net.dublin.bus.data.stop.remote.RemoteStopDataSource
import net.dublin.bus.model.Favourite
import net.dublin.bus.model.Stop

class StopRepository(application: Application) {
    private var localSource: LocalStopDataSource = LocalStopDataSource(application)
    private var remoteSource: RemoteStopDataSource = RemoteStopDataSource()
    private var localFavouriteSource: LocalStopFavouriteDataSource = LocalStopFavouriteDataSource(application)

    fun getData(): Observable<List<Stop>> {
        return localSource.getAll()
    }

    fun getStopsByLatLng(latitude: Double, longitude: Double): Single<MutableList<Stop>> {
        return localSource
                .getStopsByLatLng(latitude, longitude)
                .flatMapIterable({ it })
                .map({ it1 ->
                    it1.calculateDistance(latitude, longitude)
                    it1
                })
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

    fun getFavourites(): Observable<List<Favourite>> {
        return localFavouriteSource.getAll()
    }

    fun getStopsByText(search: String): Observable<List<Stop>> {
        return localSource.getStopsByText(search)
    }
}
