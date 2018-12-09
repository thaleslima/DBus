package net.dublin.bus.ui.view.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import net.dublin.bus.data.route.RouteComparator
import net.dublin.bus.data.route.repository.RouteRepository
import net.dublin.bus.model.Route
import java.util.*

internal class RouteViewModel(val repository: RouteRepository) : ViewModel() {
    private var stops: LiveData<List<Route>> = Transformations.map(repository.getData()) {
        Collections.sort(it, RouteComparator())
        it
    }

    fun getStops(): LiveData<List<Route>> {
        return stops
    }
}
