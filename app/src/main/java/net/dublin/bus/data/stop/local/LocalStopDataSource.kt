package net.dublin.bus.data.stop.local

import android.content.Context
import io.reactivex.Observable
import net.dublin.bus.data.BusDatabase
import net.dublin.bus.data.stop.db.StopDao
import net.dublin.bus.model.Stop

class LocalStopDataSource(context: Context) {
    private var dao: StopDao

    init {
        val db = BusDatabase.getDatabase(context)
        dao = db.getStopDao()
    }

    fun getAll(): Observable<List<Stop>> {
        return Observable.fromCallable { dao.getStops() }
    }

    fun saveAll(stops: List<Stop>) {
        dao.saveAllStops(stops)
    }
}