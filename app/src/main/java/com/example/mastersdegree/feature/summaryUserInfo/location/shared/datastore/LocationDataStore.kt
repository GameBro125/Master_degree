package com.example.mastersdegree.feature.summaryUserInfo.location.shared.datastore

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.entity.LocationEntity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Интерфейс для абстрагирования источника локации
interface ILocationDataStore {
    val currentLocation: StateFlow<LocationEntity?>
    fun requestLocationUpdates()
    fun stopLocationUpdates()
}

// Реализация через FusedLocationProviderClient
class LocationDataStore(context: Context) : ILocationDataStore {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context.applicationContext)

    private var lastKnownLocation: Location? = null

    private val _currentLocation = MutableStateFlow<LocationEntity?>(null)
    override val currentLocation: StateFlow<LocationEntity?> = _currentLocation.asStateFlow()

    private val locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000L).build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return
            updateLocation(location)
        }
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateLocation(location: Location) {
        val distance = lastKnownLocation?.distanceTo(location) ?: Float.MAX_VALUE
        if (distance > 10f) { // обновляем, если изменение > 10 метров
            lastKnownLocation = location
            _currentLocation.value = LocationEntity(
                longitude = location.longitude,
                latitude = location.latitude
            )
        }
    }
}
