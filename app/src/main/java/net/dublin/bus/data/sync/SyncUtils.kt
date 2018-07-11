package net.dublin.bus.data.sync

import android.content.Context
import com.firebase.jobdispatcher.*
import java.util.concurrent.TimeUnit

object SyncUtils {
    private const val SYNC_TAG = "dublin-bus-sync"
    private const val SYNC_INTERVAL_HOURS = 12
    private val SYNC_INTERVAL_SECONDS = TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS.toLong()).toInt()
    private val SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3

    @Synchronized
    fun initialize(context: Context) {
        val driver = GooglePlayDriver(context.applicationContext)
        val dispatcher = FirebaseJobDispatcher(driver)

        val syncSunshineJob = dispatcher.newJobBuilder()
                .setService(SyncFirebaseJobService::class.java)
                .setTag(SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build()

        dispatcher.schedule(syncSunshineJob)
    }
}