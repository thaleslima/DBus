package net.dublin.bus.ui.view.route

import net.dublin.bus.model.Route
import net.dublin.bus.data.stop.repository.RouteRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription


class RoutePresenter(private val view: RouteContract.View,
                     private val repository: RouteRepository) : RouteContract.Presenter {

    private val subscriptions: CompositeSubscription = CompositeSubscription()

    override fun unsubscribe() {
        this.subscriptions.clear()
    }

    override fun loadData() {
        view.showProgress()

        val subscription = repository.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<Route>>() {
                    override fun onCompleted() {
                        view.hideProgress()
                    }

                    override fun onError(e: Throwable) {
                        onError()
                    }

                    override fun onNext(data: List<Route>) {
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

    private fun onNextData(data: List<Route>) {
        view.showData(data)
    }
}
