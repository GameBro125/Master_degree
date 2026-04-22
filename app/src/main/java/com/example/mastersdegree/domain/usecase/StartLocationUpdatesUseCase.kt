package com.example.mastersdegree.domain.usecase

import com.example.mastersdegree.domain.repository.DeviceSensorsRepository

class StartLocationUpdatesUseCase(
    private val sensorsRepository: DeviceSensorsRepository
) {
    operator fun invoke() = sensorsRepository.startLocationUpdates()
}
