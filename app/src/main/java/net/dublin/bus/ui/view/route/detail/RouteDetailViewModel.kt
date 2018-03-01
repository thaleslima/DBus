package net.dublin.bus.ui.view.route.detail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Stop
import android.arch.lifecycle.LiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


internal class RouteDetailViewModel(val repository: RouteRepository) : ViewModel() {
    private var stops: MutableLiveData<List<Stop>> = MutableLiveData()

    fun getStops(): LiveData<List<Stop>> {
        return stops
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
}
