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
import com.example.mastersdegree.feature.location.shared.api.LocationService
import com.example.mastersdegree.feature.location.shared.datastore.LocationDataStore
import com.example.mastersdegree.data.remote.MagneticRetrofitStore
import com.example.mastersdegree.data.remote.RemoteDataEntity
import com.example.mastersdegree.data.remote.api.DataService
import com.example.mastersdegree.feature.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.magnetic.shared.api.MagneticService
import com.example.mastersdegree.feature.magnetic.shared.datastore.MagneticSensorDataStore
import com.example.mastersdegree.feature.magnetic.shared.entity.MagneticFieldEntity
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

    // TODO Перенести в конструктор // TODO Удалить устаревшие запросы
    private val apiMagneticFieldService by lazy { MagneticRetrofitStore.body.create(MagneticService::class.java) }
    private val apiLocationService by lazy { MagneticRetrofitStore.body.create(LocationService::class.java) }
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

    // TODO убрать context // TODO удалить устревшие функции
    fun sendMagneticFieldData(context: Context) {
        viewModelScope.launch {
            try {
                val magneticField = _state.value.magneticField
                if (magneticField == null) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.sending_error),
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }

                val response = apiMagneticFieldService.sendMagneticField(magneticField)
                if (response.isSuccessful) {
                    // Обработка успешного ответа
                    val vector = response.body()?.vector
                    Toast.makeText(
                        context,
                        context.getString(R.string.data_sent),
                        Toast.LENGTH_LONG
                    ).show()
                } else
                    Toast.makeText(
                        context,
                        context.getString(R.string.sending_error),
                        Toast.LENGTH_LONG
                    ).show()

            } catch (e: Exception) {
                println("Ошибка: ${e.localizedMessage}")
            }
        }
    }

    // TODO убрать context // TODO удалить устревшие функции
    fun sendLocationData(context: Context) {
        viewModelScope.launch {
            try {
                val location = _state.value.location
                if (location == null) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.sending_error),
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }

                val response = apiLocationService.sendLocation(location)
                if (response.isSuccessful) {
                    // Обработка успешного ответа
                    Toast.makeText(
                        context,
                        context.getString(R.string.data_sent),
                        Toast.LENGTH_LONG
                    ).show()
                } else
                    Toast.makeText(
                        context,
                        context.getString(R.string.sending_error),
                        Toast.LENGTH_LONG
                    ).show()

            } catch (e: Exception) {
                println("Ошибка: ${e.localizedMessage}")
            }
        }
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

                if (data.location == null || data.magneticField == null) {
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
                } else
                    Toast.makeText(
                        context,
                        context.getString(R.string.sending_error),
                        Toast.LENGTH_LONG
                    ).show()

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
                val magneticSensorDataStore =
                    this[MAGNETIC_SENSOR_MANAGER_KEY] as MagneticSensorDataStore
                MainViewModel(locationDataStore, magneticSensorDataStore)
            }
        }
    }
}
