package com.example.mastersdegree.data.sensor.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.mastersdegree.domain.model.Location as DomainLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationSensorDataSource(context: Context) {
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context.applicationContext)

    private var lastKnownLocation: Location? = null

    private val _currentLocation = MutableStateFlow<DomainLocation?>(null)
    val currentLocation: StateFlow<DomainLocation?> = _currentLocation.asStateFlow()

    private val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000L).build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation ?: return
            updateLocation(location)
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateLocation(location: Location) {
        val distance = lastKnownLocation?.distanceTo(location) ?: Float.MAX_VALUE
        if (distance > 10f) {
            lastKnownLocation = location
            _currentLocation.value = DomainLocation(
                longitude = location.longitude,
                latitude = location.latitude
            )
        }
    }
}
