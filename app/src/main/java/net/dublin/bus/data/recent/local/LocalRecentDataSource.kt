package net.dublin.bus.data.recent.local

import android.content.Context
import io.reactivex.Observable
import net.dublin.bus.data.BusDatabase
import net.dublin.bus.data.recent.db.RecentDao
import net.dublin.bus.model.Recent
import java.io.IOException

class LocalRecentDataSource(context: Context) {
    private var dao: RecentDao

    init {
        val db = BusDatabase.getDatabase(context)
        dao = db.getRecentDao()
    }

    fun getAll(): Observable<List<Recent>> {
        return Observable.fromCallable { dao.getRecent() }
    }

    fun save(recent: Recent): Observable<Boolean> {
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