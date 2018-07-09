package net.dublin.bus.ui.view.search

import net.dublin.bus.model.Recent
import net.dublin.bus.model.Route
import net.dublin.bus.model.Stop
import net.dublin.bus.ui.view.BasePresenter

interface SearchContract {
    interface View {
        fun showStops(data: List<Stop>)

        fun showRoutes(data: List<Route>)

        fun showRecent(data: List<Recent>)

        fun resetSearch()

        fun cleanData()

        fun cleanRecentData()

        fun showRoute(item: Route)

        fun showStop(item: Stop)
    }

    interface Presenter : BasePresenter {
        fun loadRecent()

        fun loadSearch(search: String)

        fun reset()

        fun loadRoute(item: Route)

        fun loadStop(item: Stop)

        fun loadRecentRoute(item: String)

        fun loadRecentStop(item: String)
    }
}
