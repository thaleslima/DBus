package net.dublin.bus.data.route.local

import android.arch.lifecycle.LiveData
import android.content.Context
import io.reactivex.Observable
import net.dublin.bus.data.BusDatabase
import net.dublin.bus.data.route.db.RouteDao
import net.dublin.bus.model.Route

class LocalRouteDataSource(context: Context) {
    private var dao: RouteDao

    init {
        val db = BusDatabase.getDatabase(context)
        dao = db.getRouteDao()
    }

    fun getAll(): LiveData<List<Route>> {
        return dao.getRoutes()
    }

    fun getRouteByNumber(number: String): Observable<Route> {
        return Observable.fromCallable { dao.getRouteByNumber(number) }
    }

    fun saveAll(routes: List<Route>) {
        dao.saveAllRoutes(routes)
    }

    fun getStopsByText(search: String): Observable<List<Route>> {
        return Observable.fromCallable { dao.getStopsByText(search) }
    }
}