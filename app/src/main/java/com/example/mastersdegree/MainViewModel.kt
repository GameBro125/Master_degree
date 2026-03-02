package com.example.mastersdegree

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mastersdegree.data.remote.RemoteDataEntity
import com.example.mastersdegree.data.remote.api.DataService
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.datastore.LocationDataStore
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.datastore.MagneticSensorDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val locationDataStore: LocationDataStore,
    private val magneticSensorDataStore: MagneticSensorDataStore,
    private val apiDataService: DataService
) : ViewModel() {

    private val _state = MutableStateFlow(MainViewState())
    val state: StateFlow<MainViewState> = _state.asStateFlow()

    private val _sendResult = MutableStateFlow<SendDataResult>(SendDataResult.Idle)

    val sendResult: StateFlow<SendDataResult> = _sendResult.asStateFlow()

    init {
        observeLocation()
        observeMagneticField()
    }

    private fun observeLocation() {
        snapshotFlow { locationDataStore.currentLocation }
            .onEach { location ->
                _state.update {
                    it.copy(
                        location = LocationEntity(
                            longitude = location.value?.longitude ?: 0.0,
                            latitude = location.value?.latitude ?: 0.0
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeMagneticField() {
        snapshotFlow { magneticSensorDataStore.magneticField }
            .onEach { field ->
                _state.update { it.copy(magneticField = field) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Отправка данных на сервер с управлением состоянием через SendDataResult.
     */
    fun sendData() {
        val location = _state.value.location
        val magneticField = _state.value.magneticField

        if (location == null || magneticField == null) {
            _sendResult.value =
                SendDataResult.Error("Данные локации или магнитного поля отсутствуют")
            return
        }
        val data = RemoteDataEntity(
            location = location,
            magneticField = magneticField
        )

        viewModelScope.launch {
            _sendResult.value = SendDataResult.Loading
            try {
                val response = apiDataService.sendData(data)

                if (response.isSuccessful) {
                    _sendResult.value = SendDataResult.Success
                } else {
                    _sendResult.value = SendDataResult.Error("Ошибка сервера: ${response.code()}")
                }
            } catch (e: Exception) {
                _sendResult.value =
                    SendDataResult.Error("Ошибка отправки данных: ${e.localizedMessage}")
            }
        }
    }

    companion object {
        const val GPS_REQUEST_LOCATION = 1001

        fun provideFactory(
            locationDataStore: LocationDataStore,
            magneticSensorDataStore: MagneticSensorDataStore,
            apiDataService: DataService
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel(locationDataStore, magneticSensorDataStore, apiDataService)
            }
        }
    }
}

sealed interface SendDataResult {
    object Idle : SendDataResult
    object Loading : SendDataResult
    object Success : SendDataResult
    data class Error(val message: String) : SendDataResult
}