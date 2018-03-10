package net.dublin.bus.data.recent.repository

import android.content.Context
import io.reactivex.Observable
import net.dublin.bus.data.recent.local.LocalRecentDataSource
import net.dublin.bus.data.route.RouteComparator
import net.dublin.bus.data.route.remote.RouteDataSource
import net.dublin.bus.data.stop.local.LocalRouteDataSource
import net.dublin.bus.model.Recent
import net.dublin.bus.model.RecentType
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import java.util.*

class RecentRepository(context: Context) {
    private var localSource: LocalRecentDataSource = LocalRecentDataSource(context)

    fun getData(): Observable<List<Recent>> {
        return localSource.getAll()
    }

    fun getSaveRoute(number: String): Observable<Boolean> {
        return localSource.save(Recent(number, RecentType.ROUTE.ordinal, Date().time))
    }

    fun getSaveStop(number: String): Observable<Boolean> {
        return localSource.save(Recent(number, RecentType.STOP.ordinal, Date().time))
    }
}
