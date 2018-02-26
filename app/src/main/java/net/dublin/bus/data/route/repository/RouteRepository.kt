package net.dublin.bus.data.route.repository

import io.reactivex.Observable
import net.dublin.bus.model.Route
import net.dublin.bus.data.route.remote.RouteDataSource
import net.dublin.bus.model.Stop

class RouteRepository {
    fun getData(): Observable<List<Route>> {
        return RouteDataSource().getData()
    }

    fun getDataDetail(route: String, direction: String): Observable<List<Stop>> {
        return RouteDataSource().getDataDetail(route, direction)
    }
}
