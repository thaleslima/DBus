package net.dublin.bus.data.stop.repository

import net.dublin.bus.model.Route
import net.dublin.bus.data.stop.remote.RouteDataSource
import net.dublin.bus.model.Stop
import rx.Observable

class RouteRepository {
    fun getData(): Observable<List<Route>> {
        return RouteDataSource().getData()
    }

    fun getDataDetail(route: String, direction: String): Observable<List<Stop>> {
        return RouteDataSource().getDataDetail(route, direction)
    }
}
