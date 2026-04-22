package com.example.mastersdegree.feature.summary.presentation

import com.example.mastersdegree.domain.model.Location
import com.example.mastersdegree.domain.model.MagneticField

data class SummaryState(
    val location: Location? = null,
    val magneticField: MagneticField = MagneticField(),
    val sendResult: SendResult = SendResult.Idle,
)

sealed interface SendResult {
    data object Idle : SendResult
    data object Loading : SendResult
    data object Success : SendResult
    data class Error(val message: String) : SendResult
}

sealed interface SummaryIntent {
    data object RequestLocationUpdates : SummaryIntent
    data object StopLocationUpdates : SummaryIntent
    data object SendSummary : SummaryIntent
    data object ConsumeSendResult : SummaryIntent
}

sealed interface SummaryEffect {
    data class ShowMessage(val message: String) : SummaryEffect
}
