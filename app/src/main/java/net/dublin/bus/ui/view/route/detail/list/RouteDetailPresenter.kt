package net.dublin.bus.ui.view.route.detail.list

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Stop


class RouteDetailPresenter(private val view: RouteDetailContract.View,
                           private val repository: RouteRepository,
                           private val route: String,
                           private val direction: String) : RouteDetailContract.Presenter {

    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        this.subscriptions.clear()
    }

    override fun loadData() {
        view.showProgress()

        val subscription = repository.getDataDetail(route, direction)
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

    private fun onNextData(data: List<Stop>) {
        view.showData(data)
    }
}
