package net.dublin.bus.data.route.repository

import android.content.Context
import io.reactivex.Observable
import net.dublin.bus.data.route.RouteComparator
import net.dublin.bus.data.route.remote.RouteDataSource
import net.dublin.bus.data.stop.local.LocalRouteDataSource
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import java.util.*

class RouteRepository(context: Context){
    private var localSource: LocalRouteDataSource = LocalRouteDataSource(context)
    private var remoteSource: RouteDataSource = RouteDataSource()

    fun getData(): Observable<List<Route>> {
        return localSource.getAll().map {
            Collections.sort(it, RouteComparator())
            it
        }
    }

    fun getDataDetail(route: String, direction: String): Observable<List<Stop>> {
        return RouteDataSource().getDataDetail(route, direction)
    }

    fun getRoutesByStopNumber(stopNumber: String): Observable<String> {
        return remoteSource
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
}
