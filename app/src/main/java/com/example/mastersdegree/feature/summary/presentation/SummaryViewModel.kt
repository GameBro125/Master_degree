package com.example.mastersdegree.feature.summary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mastersdegree.domain.model.SummaryPayload
import com.example.mastersdegree.domain.usecase.ObserveLocationUseCase
import com.example.mastersdegree.domain.usecase.ObserveMagneticFieldUseCase
import com.example.mastersdegree.domain.usecase.SendSummaryDataUseCase
import com.example.mastersdegree.domain.usecase.StartLocationUpdatesUseCase
import com.example.mastersdegree.domain.usecase.StopLocationUpdatesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SummaryViewModel(
    private val observeLocationUseCase: ObserveLocationUseCase,
    private val observeMagneticFieldUseCase: ObserveMagneticFieldUseCase,
    private val startLocationUpdatesUseCase: StartLocationUpdatesUseCase,
    private val stopLocationUpdatesUseCase: StopLocationUpdatesUseCase,
    private val sendSummaryDataUseCase: SendSummaryDataUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SummaryState())
    val state: StateFlow<SummaryState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SummaryEffect>()
    val effects: SharedFlow<SummaryEffect> = _effects.asSharedFlow()
    private var monitoringJob: Job? = null

    init {
        observeLocationUseCase()
            .onEach { location ->
                _state.update { current -> current.copy(location = location) }
            }
            .launchIn(viewModelScope)

        observeMagneticFieldUseCase()
            .onEach { magnetic ->
                _state.update { current -> current.copy(magneticField = magnetic) }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: SummaryIntent) {
        when (intent) {
            SummaryIntent.RequestLocationUpdates -> startLocationUpdatesUseCase()
            SummaryIntent.StopLocationUpdates -> stopLocationUpdatesUseCase()
            SummaryIntent.ConsumeSendResult -> {
                _state.update { it.copy(sendResult = SendResult.Idle) }
            }
            SummaryIntent.SendSummary -> sendSummary()
            is SummaryIntent.SetMonitoringEnabled -> setMonitoring(intent.enabled)
            is SummaryIntent.SetSendIntervalSeconds -> {
                _state.update { it.copy(sendIntervalSeconds = intent.seconds.coerceAtLeast(1)) }
                restartMonitoringIfNeeded()
            }
        }
    }

    private fun setMonitoring(enabled: Boolean) {
        _state.update { it.copy(monitoringEnabled = enabled) }
        if (!enabled) {
            monitoringJob?.cancel()
            monitoringJob = null
            return
        }
        restartMonitoringIfNeeded()
    }

    private fun restartMonitoringIfNeeded() {
        if (!_state.value.monitoringEnabled) return

        monitoringJob?.cancel()
        monitoringJob = viewModelScope.launch {
            while (isActive && _state.value.monitoringEnabled) {
                sendSummary()
                val delayMs = _state.value.sendIntervalSeconds * 1000L
                delay(delayMs)
            }
        }
    }

    private fun sendSummary() {
        val currentState = _state.value
        val location = currentState.location

        if (location == null) {
            _state.update { it.copy(sendResult = SendResult.Error("Location is unavailable")) }
            return
        }

        val payload = SummaryPayload(
            location = location,
            magneticField = currentState.magneticField
        )

        viewModelScope.launch {
            _state.update { it.copy(sendResult = SendResult.Loading) }

            val result = sendSummaryDataUseCase(payload)
            result.onSuccess {
                _state.update { it.copy(sendResult = SendResult.Success) }
                _effects.emit(SummaryEffect.ShowMessage("Data sent"))
            }.onFailure { throwable ->
                val message = throwable.message ?: "Sending error"
                _state.update { it.copy(sendResult = SendResult.Error(message)) }
                _effects.emit(SummaryEffect.ShowMessage(message))
            }
        }
    }

    companion object {
        fun provideFactory(
            observeLocationUseCase: ObserveLocationUseCase,
            observeMagneticFieldUseCase: ObserveMagneticFieldUseCase,
            startLocationUpdatesUseCase: StartLocationUpdatesUseCase,
            stopLocationUpdatesUseCase: StopLocationUpdatesUseCase,
            sendSummaryDataUseCase: SendSummaryDataUseCase,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SummaryViewModel(
                    observeLocationUseCase = observeLocationUseCase,
                    observeMagneticFieldUseCase = observeMagneticFieldUseCase,
                    startLocationUpdatesUseCase = startLocationUpdatesUseCase,
                    stopLocationUpdatesUseCase = stopLocationUpdatesUseCase,
                    sendSummaryDataUseCase = sendSummaryDataUseCase
                )
            }
        }
    }
}
