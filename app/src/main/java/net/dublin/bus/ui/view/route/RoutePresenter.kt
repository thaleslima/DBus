package net.dublin.bus.ui.view.route

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Route

class RoutePresenter(private val view: RouteContract.View,
                     private val repository: RouteRepository) : RouteContract.Presenter {

    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        this.subscriptions.clear()
    }

    override fun loadData() {
        view.showProgress()

        val subscription = repository.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    view.hideProgress()
                    onNextData(data)
                }, {
                    onError()
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
