package net.dublin.bus.ui.view.realtime

import net.dublin.bus.data.realtime.repository.RealTimeRepository
import net.dublin.bus.model.StopData
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class RealTimePresenter(private val view: RealTimeContract.View,
                        private val repository: RealTimeRepository,
                        private val stopNumber: String) : RealTimeContract.Presenter {

    private val subscriptions: CompositeSubscription = CompositeSubscription()

    override fun unsubscribe() {
        this.subscriptions.clear()
    }

    override fun loadData() {
        view.hideNoData()
        view.hideSnackBar()

        if (view.getSizeData() == 0) {
            view.showProgress()
        } else {
            view.showProgressSwipe()
        }

        val subscription = repository.getData(stopNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<StopData>>() {
                    override fun onCompleted() {
                        view.hideProgress()
                        view.hideProgressSwipe()
                    }

                    override fun onError(e: Throwable) {
                        onError()
                    }

                    override fun onNext(data: List<StopData>) {
                        onNextData(data)
                    }
                })

        subscriptions.add(subscription)
    }

    private fun onError() {
        view.hideProgress()
        view.hideProgressSwipe()

        if (view.isNetworkAvailable()) {
            view.showSnackBarError()
        } else {
            view.showSnackBarNoConnection()
        }

        if (view.getSizeData() == 0) {
            view.showNoData()
        }
    }

    private fun onNextData(data: List<StopData>) {
        if (data.isNotEmpty()) {
            view.showData(data)
        } else {
            view.showNoData()
        }
    }
}
