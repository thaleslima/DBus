package net.dublin.bus.data.stop.local

import android.arch.lifecycle.LiveData
import android.content.Context
import io.reactivex.Observable
import net.dublin.bus.data.BusDatabase
import net.dublin.bus.data.stop.db.StopDao
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import java.io.IOException

class LocalStopDataSource(context: Context) {
    private var dao: StopDao

    init {
        val db = BusDatabase.getDatabase(context)
        dao = db.getStopDao()
    }

    fun getAll(): LiveData<List<Stop>> {
        return dao.getStops()
    }

    fun getStopsByLatLng(latitude: Double, longitude: Double): Observable<List<Stop>> {
        return Observable.fromCallable { dao.getStopsByLatLng(latitude, longitude) }
    }

    fun saveAll(stops: List<Stop>) {
        dao.saveAllStops(stops)
    }

    fun replaceAll(stops: List<Stop>) {
        dao.replaceAll(stops)
    }

    fun getStopsByText(search: String): Observable<List<Stop>> {
        return Observable.create { subscriber ->
            try {
                subscriber.onNext(dao.getStopsByText(search))
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
        return Observable.fromCallable { dao.getStopsByNumber(stopNumber) }
    }
}