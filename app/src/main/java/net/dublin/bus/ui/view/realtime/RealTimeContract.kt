package net.dublin.bus.ui.view.realtime

import net.dublin.bus.model.StopData
import net.dublin.bus.ui.view.BasePresenter

interface RealTimeContract {
    interface View {
        fun getSizeData(): Int

        fun hasNetwork(): Boolean

        fun showData(data: List<StopData>)

        fun showProgress()

        fun hideProgress()

        fun showProgressSwipe()

        fun hideProgressSwipe()

        fun showSnackBarNoConnection()

        fun showSnackBarError()

        fun showNoData()

        fun hideNoData()

        fun hideSnackBar()

        fun showLineNote(lineNote: String)

        fun hideLineNote()

        fun showFavouriteYes()

        fun showFavouriteNo()

        fun showSnackbarRemoveFavourite()

        fun showSnackbarSaveFavourite()

        fun sendFavoriteEvent(value: Boolean)
    }

    interface Presenter : BasePresenter {
        fun loadData()

        fun loadFavouriteStatus()

        fun addOrRemoveFavourite()
    }
}
