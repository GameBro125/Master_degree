package com.example.mastersdegree.domain.usecase

import com.example.mastersdegree.domain.repository.DeviceSensorsRepository

class StopLocationUpdatesUseCase(
    private val sensorsRepository: DeviceSensorsRepository
) {
    operator fun invoke() = sensorsRepository.stopLocationUpdates()
}
