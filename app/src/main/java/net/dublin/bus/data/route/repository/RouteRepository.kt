package net.dublin.bus.data.route.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import io.reactivex.Observable
import net.dublin.bus.data.BusDatabase
import net.dublin.bus.data.route.RouteComparator
import net.dublin.bus.data.route.db.RouteDao
import net.dublin.bus.data.route.remote.RouteDataSourceRemote
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import java.util.*

class RouteRepository(context: Context) {
    private var routeDao: RouteDao
    private var routeRemote: RouteDataSourceRemote

    init {
        val db = BusDatabase.getDatabase(context)
        routeDao = db.getRouteDao()
        routeRemote = RouteDataSourceRemote()
    }

    fun getData(): LiveData<List<Route>> {
        return routeDao.getRoutes()
    }

    fun getRouteByNumber(number: String): Observable<Route> {
        return Observable.fromCallable { routeDao.getRouteByNumber(number) }
    }

    fun getDataDetail(route: String, direction: String): Observable<List<Stop>> {
        return RouteDataSourceRemote().getDataDetail(route, direction)
    }

    fun getRoutesByStopNumber(stopNumber: String): Observable<String> {
        return routeRemote
                .getRoutesByStopNumber(stopNumber)
                .map {
                    Collections.sort(it, RouteComparator())
                    val set = linkedSetOf<String>()
                    for (r in it) {
                        set.add(r.number)
                    }
                    set.joinToString(separator = ", ")
                }
    }

    fun getStopsByText(search: String): Observable<List<Route>> {
        return Observable.fromCallable { routeDao.getStopsByText(search) }.map {
            Collections.sort(it, RouteComparator())
            it
        }
    }
}