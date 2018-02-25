package net.dublin.bus.ui.view.stop

import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Stop
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription


class StopPresenter(private val view: StopContract.View,
                    private val repository: StopRepository) : StopContract.Presenter {

    private val subscriptions: CompositeSubscription = CompositeSubscription()

    override fun unsubscribe() {
        this.subscriptions.clear()
    }

    override fun loadData() {
        view.showProgress()

        val subscription = repository.getData()
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
