package com.example.mastersdegree

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mastersdegree.domain.remote.createApiService
import com.example.mastersdegree.feature.location.LocationManager
import com.example.mastersdegree.feature.magnetic.MagneticSensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val locationManager: LocationManager,
    private val magneticSensorManager: MagneticSensorManager,
    private val scopeIO: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO),
    private val scopeUI: CoroutineScope = CoroutineScope(Job() + Dispatchers.Main.immediate),
) : ViewModel() {

    // TODO Пренести в конструктор
    private val apiService by lazy { createApiService() }

    private val _state = MutableStateFlow(MainViewState())
    val state = _state.asStateFlow()

    init {
        observeLocation()
        observeMagneticField()
    }

    private fun observeMagneticField() {
        snapshotFlow { magneticSensorManager.magneticField }
            .onEach { field ->
                _state.update { state ->
                    state.copy(magneticField = field)
                }
            }
            .launchIn(scopeIO)
    }

    private fun observeLocation() {
        snapshotFlow { locationManager.currentUserLocation }
            .onEach { location ->
                _state.update { state ->
                    state.copy(location = location)
                }
            }
            .launchIn(scopeIO)
    }

    // TODO убрать context
    fun sendMagneticFieldData(context: Context) {
        viewModelScope.launch {
            try {
                val magneticField = _state.value.magneticField
                if (magneticField == null) {
                    Toast.makeText(context, context.getString(R.string.sending_error), Toast.LENGTH_LONG).show()
                    return@launch
                }

                val response = apiService.sendMagneticField(magneticField)
                if (response.isSuccessful) {
                    // Обработка успешного ответа
                    val vector = response.body()?.vector
                    Toast.makeText(context, context.getString(R.string.data_sent), Toast.LENGTH_LONG).show()
                } else
                    Toast.makeText(context, context.getString(R.string.sending_error), Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                println("Ошибка: ${e.localizedMessage}")
            }
        }
    }

    companion object {
        val LOCATION_MANAGER_KEY = object : CreationExtras.Key<LocationManager> {}
        val MAGNETIC_SENSOR_MANAGER_KEY = object : CreationExtras.Key<MagneticSensorManager> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val locationManager = this[LOCATION_MANAGER_KEY] as LocationManager
                val magneticSensorManager = this[MAGNETIC_SENSOR_MANAGER_KEY] as MagneticSensorManager
                MainViewModel(locationManager, magneticSensorManager)
            }
        }
    }
}
