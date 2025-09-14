package com.example.mastersdegree

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.api.LocationService
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.datastore.LocationDataStore
import com.example.mastersdegree.data.remote.MagneticRetrofitStore
import com.example.mastersdegree.data.remote.RemoteDataEntity
import com.example.mastersdegree.data.remote.api.DataService
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.api.MagneticService
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.datastore.MagneticSensorDataStore
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
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
) : ViewModel() {

    // TODO Перенести в конструктор // TODO Удалить устаревшие запросы
    private val apiDataService by lazy { MagneticRetrofitStore.body.create(DataService::class.java) }

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


    // TODO реворкнуть эту констркцию ущербную
    // TODO убрать context
    // TODO Зачем context убрать - Toast() тогда как сделать :< ?
    fun sendData(context: Context) {
        viewModelScope.launch {
            try {
                val data: RemoteDataEntity = RemoteDataEntity(
                    location = _state.value.location!!,
                    magneticField = _state.value.magneticField!!
                )

                if (data.magneticField == null) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.sending_error),
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }

                val response = apiDataService.sendData(data)
                if (response.isSuccessful) {
                    // Обработка успешного ответа
                    Toast.makeText(
                        context,
                        context.getString(R.string.data_sent),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                println("Ошибка: ${e.localizedMessage}")
            }
        }
    }

    companion object {
        val LOCATION_MANAGER_KEY = object : CreationExtras.Key<LocationDataStore> {}
        val MAGNETIC_SENSOR_MANAGER_KEY = object : CreationExtras.Key<MagneticSensorDataStore> {}
        const val GPS_REQUEST_LOCATION = 1001

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val locationDataStore = this[LOCATION_MANAGER_KEY] as LocationDataStore
                val magneticSensorDataStore =
                    this[MAGNETIC_SENSOR_MANAGER_KEY] as MagneticSensorDataStore
                MainViewModel(locationDataStore, magneticSensorDataStore)
            }
        }
    }
}
