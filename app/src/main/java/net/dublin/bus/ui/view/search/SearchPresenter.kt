package net.dublin.bus.ui.view.search

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop

class SearchPresenter(private val view: SearchContract.View,
                      private val routeRepository: RouteRepository,
                      private val stopRepository: StopRepository) : SearchContract.Presenter {

    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        this.subscriptions.clear()
    }

    override fun loadSearch(search: String) {
        view.cleanData()

        if (search.isEmpty()) {
            return
        }

        unsubscribe()

        if (search.length >= 2) {
            val subscription1 = stopRepository.getStopsByText(search)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ data ->
                        onNextDataStop(data)
                    }, {
                        onError()
                    })

            subscriptions.add(subscription1)
        }

        val subscription2 = routeRepository.getStopsByText(search)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    onNextDataRoute(data)
                }, {
                    onError()
                })

        subscriptions.add(subscription2)
    }

    override fun reset() {
        view.resetSearch()
        view.cleanData()
    }

    private fun onError() {

    }

    private fun onNextDataStop(data: List<Stop>) {
        if (!data.isEmpty()) {
            view.showStops(data)
        }
    }

    private fun onNextDataRoute(data: List<Route>) {
        if (!data.isEmpty()) {
            view.showRoutes(data)
        }
    }
}
