package net.dublin.bus.ui.view.route.detail.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.view.route.detail.RouteDetailViewModel
import net.dublin.bus.ui.view.route.detail.RouteDetailViewModelFactory


class RouteDetailPresenter(private val view: RouteDetailContract.View) : RouteDetailContract.Presenter {

    private lateinit var model: RouteDetailViewModel

    override fun unsubscribe() {
    }

    override fun loadData() {
        view.showProgress()

        val factory = RouteDetailViewModelFactory(RouteRepository(view.getActivity()))
        model = ViewModelProviders.of(view.getActivity(), factory).get(RouteDetailViewModel::class.java)
        model.getStops().observe(view.getActivity(), Observer<List<Stop>> {
            view.hideProgress()
            it?.let { it1 -> view.showData(it1) }
        })
    }
}
