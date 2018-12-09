package net.dublin.bus.ui.view.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import net.dublin.bus.data.stop.repository.StopRepository
import net.dublin.bus.model.Favourite

internal class FavouriteViewModel(val repository: StopRepository) : ViewModel() {
    private var stops: LiveData<List<Favourite>> = repository.getFavourites()

    fun getStops(): LiveData<List<Favourite>> {
        return stops
    }
}
