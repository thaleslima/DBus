package net.dublin.bus.data.sync

import android.app.IntentService
import android.content.Intent

import net.dublin.bus.data.Repository

class SyncIntentService : IntentService("SyncIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val repository = Repository(this)
        repository.fetchData()
    }
}
