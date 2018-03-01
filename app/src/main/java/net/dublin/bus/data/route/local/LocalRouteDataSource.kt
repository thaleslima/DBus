package net.dublin.bus.data.stop.local

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

    fun getAll(): Observable<List<Route>> {
        return Observable.fromCallable { dao.getRoutes() }
    }

    fun saveAll(routes: List<Route>) {
        dao.saveAllRoutes(routes)
    }
}