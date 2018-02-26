package net.dublin.bus.ui.view.realtime

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.dublin.bus.data.realtime.repository.RealTimeRepository
import net.dublin.bus.model.StopData

class RealTimePresenter(private val view: RealTimeContract.View,
                        private val repository: RealTimeRepository,
                        private val stopNumber: String) : RealTimeContract.Presenter {

    private val subscriptions: CompositeDisposable = CompositeDisposable()

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
                .subscribe({ data ->
                    view.hideProgress()
                    view.hideProgressSwipe()
                    onNextData(data)
                }, {
                    onError()
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
