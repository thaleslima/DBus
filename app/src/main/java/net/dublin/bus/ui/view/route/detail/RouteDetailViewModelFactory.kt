package net.dublin.bus.ui.view.route.detail

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import net.dublin.bus.data.route.repository.RouteRepository

class RouteDetailViewModelFactory(private val repository: RouteRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RouteDetailViewModel(repository) as T
    }
}