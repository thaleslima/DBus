package net.dublin.bus.ui.view.route.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
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

    fun loadStops(route: String, direction: String) {
        repository.getDataDetail(route, direction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    stops.value = data
                }, {
                    stops.value = null
                })
    }

    fun loadRoutesByStopNumber(context: Context, stopNumber: String) {
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
