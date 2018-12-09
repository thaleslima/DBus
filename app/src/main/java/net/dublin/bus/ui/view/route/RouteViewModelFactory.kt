package net.dublin.bus.ui.view.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.dublin.bus.data.route.repository.RouteRepository

class RouteViewModelFactory(private val repository: RouteRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RouteViewModel(repository) as T
    }
}