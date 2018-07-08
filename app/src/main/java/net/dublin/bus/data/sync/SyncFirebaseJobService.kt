package net.dublin.bus.data.sync

import android.util.Log

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

import net.dublin.bus.data.Repository

class SyncFirebaseJobService : JobService() {
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Log.d(LOG_TAG, "Job service started")
        val repository = Repository(this)
        repository.fetchData()
        jobFinished(jobParameters, false)
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return true
    }

    companion object {
        private val LOG_TAG = SyncFirebaseJobService::class.java.simpleName
    }
}
