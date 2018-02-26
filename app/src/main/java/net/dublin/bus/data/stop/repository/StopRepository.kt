package net.dublin.bus.data.stop.repository

import io.reactivex.Observable
import net.dublin.bus.data.stop.remote.StopDataSource
import net.dublin.bus.model.Stop

class StopRepository {
    fun getData(): Observable<List<Stop>> {
        return StopDataSource().getData()
    }
}
