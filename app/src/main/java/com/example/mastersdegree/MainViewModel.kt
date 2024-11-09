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
import com.example.mastersdegree.feature.RetrofitManagers
import com.example.mastersdegree.feature.location.shared.datastore.LocationDataStore
import com.example.mastersdegree.feature.magnetic.shared.api.MagneticService
import com.example.mastersdegree.feature.magnetic.shared.datastore.MagneticSensorDataStore
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
    private val locationDataStore: LocationDataStore,
    private val magneticSensorDataStore: MagneticSensorDataStore,
    private val scopeIO: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO),
    private val scopeUI: CoroutineScope = CoroutineScope(Job() + Dispatchers.Main.immediate),
) : ViewModel() {

    // TODO Пренести в конструктор
    private val apiService by lazy { RetrofitManagers.body.create(MagneticService::class.java) }

    private val _state = MutableStateFlow(MainViewState())
    val state = _state.asStateFlow()

    init {
        observeLocation()
        observeMagneticField()
    }

    private fun observeMagneticField() {
        snapshotFlow { magneticSensorDataStore.magneticField }
            .onEach { field ->
                _state.update { state ->
                    state.copy(magneticField = field)
                }
            }
            .launchIn(scopeIO)
    }

    private fun observeLocation() {
        snapshotFlow { locationDataStore.currentLocation }
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
        val LOCATION_MANAGER_KEY = object : CreationExtras.Key<LocationDataStore> {}
        val MAGNETIC_SENSOR_MANAGER_KEY = object : CreationExtras.Key<MagneticSensorDataStore> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val locationDataStore = this[LOCATION_MANAGER_KEY] as LocationDataStore
                val magneticSensorDataStore = this[MAGNETIC_SENSOR_MANAGER_KEY] as MagneticSensorDataStore
                MainViewModel(locationDataStore, magneticSensorDataStore)
            }
        }
    }
}
