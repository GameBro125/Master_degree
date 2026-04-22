package com.example.mastersdegree.domain.repository

import com.example.mastersdegree.domain.model.Location
import com.example.mastersdegree.domain.model.MagneticField
import kotlinx.coroutines.flow.Flow

interface DeviceSensorsRepository {
    fun observeLocation(): Flow<Location?>
    fun observeMagneticField(): Flow<MagneticField>
    fun startLocationUpdates()
    fun stopLocationUpdates()
}
