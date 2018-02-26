package net.dublin.bus.data.realtime.repository

import io.reactivex.Observable
import net.dublin.bus.data.realtime.remote.RealTimeDataSource
import net.dublin.bus.model.StopData

class RealTimeRepository {
    fun getData(stopNumber: String): Observable<List<StopData>> {
        return RealTimeDataSource().getData(stopNumber)
    }
}
