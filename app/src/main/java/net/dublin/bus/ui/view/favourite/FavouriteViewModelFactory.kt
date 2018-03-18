package net.dublin.bus.ui.view.favourite

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import net.dublin.bus.data.stop.repository.StopRepository

class FavouriteViewModelFactory(private val repository: StopRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(repository) as T
    }
}