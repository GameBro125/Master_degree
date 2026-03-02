package com.example.mastersdegree.feature.summaryUserInfo.location.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mastersdegree.MainViewState
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.datastore.ILocationDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class LocationViewModel(
    private val locationDataStore: ILocationDataStore
) : ViewModel() {

    // Состояние текущей локации
    private val _state = MutableStateFlow(MainViewState())
    val state: StateFlow<MainViewState> = _state.asStateFlow()

    init {
        observeLocation()
    }

    // Наблюдаем за изменением локации
    private fun observeLocation() {
        locationDataStore.currentLocation
            .onEach { location ->
                _state.update { it.copy(location = location) }
            }
            .launchIn(viewModelScope)
    }

    // Запуск обновлений локации
    fun requestLocationUpdates() {
        locationDataStore.requestLocationUpdates()
    }

    // Остановка обновлений, если нужно
    fun stopLocationUpdates() {
        locationDataStore.stopLocationUpdates()
    }

    companion object {
        /**
         * Фабрика для безопасного создания LocationViewModel с зависимостью.
         */
        fun provideFactory(locationDataStore: ILocationDataStore): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    LocationViewModel(locationDataStore)
                }
            }
    }
}
