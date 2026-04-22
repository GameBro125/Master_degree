package com.example.mastersdegree.data.repository

import com.example.mastersdegree.data.sensor.location.LocationSensorDataSource
import com.example.mastersdegree.data.sensor.magnetic.MagneticSensorDataSource
import com.example.mastersdegree.domain.model.Location
import com.example.mastersdegree.domain.model.MagneticField
import com.example.mastersdegree.domain.repository.DeviceSensorsRepository
import kotlinx.coroutines.flow.Flow

class DeviceSensorsRepositoryImpl(
    private val locationDataSource: LocationSensorDataSource,
    private val magneticDataSource: MagneticSensorDataSource,
) : DeviceSensorsRepository {
    override fun observeLocation(): Flow<Location?> = locationDataSource.currentLocation

    override fun observeMagneticField(): Flow<MagneticField> = magneticDataSource.magneticField

    override fun startLocationUpdates() {
        locationDataSource.requestLocationUpdates()
    }

    override fun stopLocationUpdates() {
        locationDataSource.stopLocationUpdates()
    }
}
