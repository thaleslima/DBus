package net.dublin.bus.data

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.dublin.bus.common.*
import net.dublin.bus.data.route.db.RouteDao
import net.dublin.bus.data.stop.db.StopDao
import net.dublin.bus.data.sync.SyncIntentService
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.view.main.MainActivity

class Repository(val context: Context) {
    private var routeDao: RouteDao
    private var stopDao: StopDao

    init {
        val db = BusDatabase.getDatabase(context)
        routeDao = db.getRouteDao()
        stopDao = db.getStopDao()
    }


    fun initRepository() {
        val appExecutors = AppExecutors.instance
        appExecutors.diskIO().execute {
            val jsonStops = context.readStringFromFile("stops.json")
            val jsonRoutes = context.readStringFromFile("routes.json")
            val g = Gson()
            val stopList: List<Stop> = g.fromJson(jsonStops, object : TypeToken<List<Stop>>() {}.type)
            val routesList: List<Route> = g.fromJson(jsonRoutes, object : TypeToken<List<Route>>() {}.type)
            routeDao.saveAllRoutes(routesList)
            stopDao.saveAllStops(stopList)
            Log.d(Repository::class.java.name, "bd created")
        }
    }

    fun fetchData() {
        val appExecutors = AppExecutors.instance
        appExecutors.networkIO().execute {
            try {
                var routesStringRemote = NetworkUtils.getResponseFromHttpUrl(Constants.API_URL_LAST_UPDATE, false)
                routesStringRemote = routesStringRemote.trim()

                val lastDateLocal = getLastDate()

                if (routesStringRemote != lastDateLocal) {
                    downloadAndUpdateData(routesStringRemote)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(Repository::class.java.name, "Service exception: ${e.localizedMessage}")
                Analytics.sendSyncStatus(context, false)
            }
        }
    }

    fun startFetchService() {
        val intentToFetch = Intent(context, SyncIntentService::class.java)
        context.startService(intentToFetch)
        Log.d(MainActivity::class.java.name, "Service created")
    }

    private fun downloadAndUpdateData(routesStringRemote: String) {
        val routesString = NetworkUtils.getResponseFromHttpUrl(Constants.API_URL_STORAGE_ROUTES + routesStringRemote, true)
        val stopString = NetworkUtils.getResponseFromHttpUrl(Constants.API_URL_STORAGE_STOPS + routesStringRemote, true)

        val g = Gson()
        val routeList = g.fromJson<List<Route>>(routesString, object : TypeToken<List<Route>>() {}.type)
        val stopList = g.fromJson<List<Stop>>(stopString, object : TypeToken<List<Stop>>() {}.type)

        if (!routeList.isEmpty()) {
            routeDao.replaceAll(routeList)
        }

        if (!stopList.isEmpty()) {
            stopDao.replaceAll(stopList)
        }

        setLastDate(routesStringRemote)
        Analytics.sendSyncStatus(context, true)
        Log.d(Repository::class.java.name, "Service executed")
    }

    private fun getLastDate(): String {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getString(PREF_LAST_DATE, "")
    }

    private fun setLastDate(lastDate: String) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val spe = sp.edit()
        spe.putString(PREF_LAST_DATE, lastDate)
        spe.apply()
    }

    companion object {
        private const val PREF_LAST_DATE = "pref-last-date"
    }
}
