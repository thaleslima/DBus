package net.dublin.bus.data.stop.repository

import net.dublin.bus.data.stop.remote.StopDataSource
import net.dublin.bus.model.Stop
import rx.Observable

class StopRepository {
    fun getData(): Observable<List<Stop>> {
        return StopDataSource().getData()
    }
}
