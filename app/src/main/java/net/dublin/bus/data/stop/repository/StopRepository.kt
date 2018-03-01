package net.dublin.bus.data.stop.repository

import android.app.Application
import io.reactivex.Observable
import net.dublin.bus.data.stop.local.LocalStopDataSource
import net.dublin.bus.model.Stop

class StopRepository(application: Application) {
    private var localSource: LocalStopDataSource = LocalStopDataSource(application)

    fun getData(): Observable<List<Stop>> {
        return localSource.getAll()

        //return RemoteStopDataSource().getData().doOnNext { localSource.saveAll(it) }
    }
}
