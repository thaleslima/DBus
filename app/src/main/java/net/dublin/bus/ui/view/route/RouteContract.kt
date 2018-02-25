package net.dublin.bus.ui.view.route

import net.dublin.bus.model.Route
import net.dublin.bus.ui.view.BasePresenter

interface RouteContract {
    interface View {
        fun isNetworkAvailable(): Boolean

        fun showData(data: List<Route>)

        fun showProgress()

        fun hideProgress()

        fun showSnackBarNoConnection()

        fun showSnackBarError()
    }

    interface Presenter : BasePresenter {
        fun loadData()
    }
}
