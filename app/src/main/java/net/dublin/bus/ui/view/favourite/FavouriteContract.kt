package net.dublin.bus.ui.view.favourite

import net.dublin.bus.model.Favourite
import net.dublin.bus.model.Route
import net.dublin.bus.ui.view.BasePresenter

interface FavouriteContract {
    interface View {
        fun isNetworkAvailable(): Boolean

        fun showData(data: List<Favourite>)

        fun showNoData()

        fun showSnackBarNoConnection()

        fun showSnackBarError()
    }

    interface Presenter : BasePresenter {
        fun loadData()
    }
}
