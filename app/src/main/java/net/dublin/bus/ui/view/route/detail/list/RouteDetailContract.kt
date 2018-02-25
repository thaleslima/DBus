package net.dublin.bus.ui.view.route.detail.list

import net.dublin.bus.model.Stop
import net.dublin.bus.ui.view.BasePresenter

interface RouteDetailContract {
    interface View {
        fun isNetworkAvailable(): Boolean

        fun showData(data: List<Stop>)

        fun showProgress()

        fun hideProgress()

        fun showSnackBarNoConnection()

        fun showSnackBarError()
    }

    interface Presenter : BasePresenter {
        fun loadData()
    }
}
