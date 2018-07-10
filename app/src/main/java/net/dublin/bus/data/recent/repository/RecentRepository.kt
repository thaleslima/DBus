package net.dublin.bus.data.recent.repository

import android.content.Context
import io.reactivex.Observable
import net.dublin.bus.data.BusDatabase
import net.dublin.bus.data.recent.db.RecentDao
import net.dublin.bus.model.Recent
import net.dublin.bus.model.RecentType
import java.io.IOException
import java.util.*

class RecentRepository(context: Context) {
    private var dao: RecentDao

    init {
        val db = BusDatabase.getDatabase(context)
        dao = db.getRecentDao()
    }

    fun getData(): Observable<List<Recent>> {
        return Observable.fromCallable { dao.getRecent() }
    }

    fun getSaveRoute(number: String): Observable<Boolean> {
        return save(Recent(number, RecentType.ROUTE.ordinal, Date().time))
    }

    fun getSaveStop(number: String): Observable<Boolean> {
        return save(Recent(number, RecentType.STOP.ordinal, Date().time))
    }

    private fun save(recent: Recent): Observable<Boolean> {
        return Observable.create { subscriber ->
            try {
                dao.saveRecent(recent)
                subscriber.onNext(true)
                subscriber.onComplete()
            } catch (e: IOException) {
                e.localizedMessage
                if (!subscriber.isDisposed) {
                    subscriber.onError(e)
                }
            }
        }
    }
}
