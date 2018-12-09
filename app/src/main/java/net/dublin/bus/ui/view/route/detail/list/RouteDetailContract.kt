package net.dublin.bus.ui.view.route.detail.list

import androidx.fragment.app.FragmentActivity
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.view.BasePresenter

interface RouteDetailContract {
    interface View {
        fun showData(data: List<Stop>)

        fun showProgress()

        fun hideProgress()

        fun showSnackBarNoConnection()

        fun showSnackBarError()

        fun requireActivity(): FragmentActivity
    }

    interface Presenter : BasePresenter {
        fun loadData()
    }
}
