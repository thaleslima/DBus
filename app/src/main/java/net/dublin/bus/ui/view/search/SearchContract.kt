package net.dublin.bus.ui.view.search

import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import net.dublin.bus.model.StopData
import net.dublin.bus.ui.view.BasePresenter

interface SearchContract {
    interface View {
        fun showStops(data: List<Stop>)

        fun showRoutes(data: List<Route>)

        fun resetSearch()

        fun cleanData()
    }

    interface Presenter : BasePresenter {
        fun loadSearch(search: String)

        fun reset()
    }
}
