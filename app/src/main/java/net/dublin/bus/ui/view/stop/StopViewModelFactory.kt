package net.dublin.bus.ui.view.stop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.dublin.bus.data.stop.repository.StopRepository

class StopViewModelFactory(private val repository: StopRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StopViewModel(repository) as T
    }
}