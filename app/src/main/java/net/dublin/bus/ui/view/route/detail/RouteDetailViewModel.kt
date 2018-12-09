package net.dublin.bus.ui.view.route.detail

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.dublin.bus.R
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Stop

internal class RouteDetailViewModel(val repository: RouteRepository) : ViewModel() {
    private var stops: MutableLiveData<List<Stop>> = MutableLiveData()
    private var routes: MutableLiveData<String> = MutableLiveData()

    fun getStops(): LiveData<List<Stop>> {
        return stops
    }

    fun getRoutes(): LiveData<String> {
        return routes
    }

    fun cleanRoutes() {
        routes.value = null
    }

    fun loadStops(route: String, direction: String) {
        if (stops.value == null) {
            reloadStops(route, direction)
        }
    }

    @SuppressLint("CheckResult")
    fun reloadStops(route: String, direction: String) {
        repository.getDataDetail(route, direction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    stops.value = data
                }, {
                    stops.value = null
                })
    }

    @SuppressLint("CheckResult")
    fun loadRoutesByStopNumber(context: Context, stopNumber: String) {
        if (routes.value == null) {
            repository.getRoutesByStopNumber(stopNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ data ->
                        routes.value = data
                    }, {
                        routes.value = context.getString(R.string.route_detail_error)
                    })
        }
    }
}
