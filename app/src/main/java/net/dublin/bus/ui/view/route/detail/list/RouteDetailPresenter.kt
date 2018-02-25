package net.dublin.bus.ui.view.route.detail.list

import net.dublin.bus.data.stop.repository.RouteRepository
import net.dublin.bus.model.Stop
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription


class RouteDetailPresenter(private val view: RouteDetailContract.View,
                           private val repository: RouteRepository,
                           private val route: String,
                           private val direction: String) : RouteDetailContract.Presenter {

    private val subscriptions: CompositeSubscription = CompositeSubscription()

    override fun unsubscribe() {
        this.subscriptions.clear()
    }

    override fun loadData() {
        view.showProgress()

        val subscription = repository.getDataDetail(route, direction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<Stop>>() {
                    override fun onCompleted() {
                        view.hideProgress()
                    }

                    override fun onError(e: Throwable) {
                        onError()
                    }

                    override fun onNext(data: List<Stop>) {
                        onNextData(data)
                    }
                })

        subscriptions.add(subscription)
    }

    private fun onError() {
        view.hideProgress()

        if (view.isNetworkAvailable()) {
            view.showSnackBarError()
        } else {
            view.showSnackBarNoConnection()
        }
    }

    private fun onNextData(data: List<Stop>) {
        view.showData(data)
    }
}
