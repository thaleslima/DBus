package net.dublin.bus.ui.view.search

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.dublin.bus.data.recent.repository.RecentRepository
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Recent
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop

class SearchPresenter(private val view: SearchContract.View,
                      private val routeRepository: RouteRepository,
                      private val stopRepository: StopRepository,
                      private val recentRepository: RecentRepository) : SearchContract.Presenter {
    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        this.subscriptions.clear()
    }

    override fun loadRoute(item: Route) {
        recentRepository.getSaveRoute(item.number).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({})
        view.showRoute(item)
    }

    override fun loadStop(item: Stop) {
        recentRepository.getSaveStop(item.stopNumber).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({})
        view.showStop(item)
    }

    override fun loadRecentRoute(item: String) {
        view.showRoute(Route(item))
    }

    override fun loadRecentStop(item: String) {
        stopRepository.getStopsByNumber(item)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    view.showStop(data)
                }, {
                    onError()
                })
    }


    override fun loadRecent() {
        recentRepository.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    view.showRecent(data)
                }, {
                    onError()
                })
    }

    override fun loadSearch(search: String) {
        view.cleanData()
        unsubscribe()

        if (search.isEmpty()) {
            loadRecent()
            return
        }

        view.cleanRecentData()

        if (search.length >= 2) {
            val subscription1 = stopRepository.getStopsByText(search)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ data ->
                        view.showStops(data)
                    }, {
                        onError()
                    })

            subscriptions.add(subscription1)
        }

        val subscription2 = routeRepository.getStopsByText(search)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    view.showRoutes(data)
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
}
