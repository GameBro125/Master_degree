package com.example.mastersdegree.feature.summaryUserInfo.location.viewModel

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mastersdegree.MainViewModel
import com.example.mastersdegree.MainViewModel.Companion.LOCATION_MANAGER_KEY
import com.example.mastersdegree.MainViewModel.Companion.MAGNETIC_SENSOR_MANAGER_KEY
import com.example.mastersdegree.MainViewState
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.datastore.LocationDataStore
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.datastore.MagneticSensorDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class LocationViewModel(
    private val locationDataStore: LocationDataStore,
    private val scopeIO: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO),
) : ViewModel() {

    private val _state = MutableStateFlow(MainViewState())

    init {
        observeLocation()
    }

    private fun observeLocation() {
        snapshotFlow { locationDataStore.currentLocation }
            .onEach { location ->
                _state.update { state ->
                    state.copy(location = location)
                }
            }.launchIn(scopeIO)
    }
    companion object {
        val LOCATION_MANAGER_KEY = object : CreationExtras.Key<LocationDataStore> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val locationDataStore = this[LOCATION_MANAGER_KEY] as LocationDataStore
                LocationViewModel(locationDataStore)
            }
        }
    }
}
