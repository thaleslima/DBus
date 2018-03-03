package net.dublin.bus.ui.view.realtime

import android.text.TextUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.dublin.bus.data.realtime.repository.RealTimeRepository
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Favourite
import net.dublin.bus.model.StopData

//import java.util.*

class RealTimePresenter(private val view: RealTimeContract.View,
                        private val repository: RealTimeRepository,
                        private val stopRepository: StopRepository,
                        private val stopNumber: String,
                        private val description: String) : RealTimeContract.Presenter {

    private val subscriptions: CompositeDisposable = CompositeDisposable()
    private var isFavorite: Boolean = false

    override fun unsubscribe() {
        this.subscriptions.clear()
    }

    override fun loadData() {
        view.hideNoData()
        view.hideSnackBar()
        view.hideLineNote()

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

    override fun loadFavouriteStatus() {
        stopRepository.isFavourite(stopNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    isFavorite = data
                    if (data) {
                        view.showFavouriteYes()
                    } else {
                        view.showFavouriteNo()
                    }
                })
    }

    override fun addOrRemoveFavourite() {
        val favourite = Favourite(stopNumber, description = description)

        val observable = if (!isFavorite) {
            stopRepository.saveFavourite(favourite)
        } else {
            stopRepository.removeFavourite(stopNumber)
        }

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    isFavorite = if (!isFavorite) {
                        view.showFavouriteYes()
                        view.showSnackbarSaveFavourite()
                        true
                    } else {
                        view.showFavouriteNo()
                        view.showSnackbarRemoveFavourite()
                        false
                    }
                })
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
            var showLineNote = false
            var hasData = false
            var lineNote = ""

            data.forEach {
                if (!TextUtils.isEmpty(it.lineNote)) {
                    showLineNote = true
                    lineNote = it.lineNote ?: ""
                }

                if (!TextUtils.isEmpty(it.destinationName)) {
                    hasData = true
                }
            }

            if (showLineNote) {
                view.showLineNote(lineNote)
            }

            if (hasData) {
                view.showData(data)
                return
            }
        }
        view.showNoData()
    }
}
