package net.dublin.bus.ui.view.realtime

import net.dublin.bus.model.StopData
import net.dublin.bus.ui.view.BasePresenter

interface RealTimeContract {
    interface View {
        fun getSizeData(): Int

        fun isNetworkAvailable(): Boolean

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
    }

    interface Presenter : BasePresenter {
        fun loadData()
    }
}
