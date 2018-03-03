package net.dublin.bus.ui.view.favourite

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Favourite

class FavouritePresenter(private val view: FavouriteContract.View,
                         private val repository: StopRepository) : FavouriteContract.Presenter {

    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun unsubscribe() {
        this.subscriptions.clear()
    }

    override fun loadData() {
        val subscription = repository.getFavourites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    onNextData(data)
                }, {
                    onError()
                })

        subscriptions.add(subscription)
    }

    private fun onError() {
        if (view.isNetworkAvailable()) {
            view.showSnackBarError()
        } else {
            view.showSnackBarNoConnection()
        }
    }

    private fun onNextData(data: List<Favourite>) {
        view.showData(data)
    }
}
