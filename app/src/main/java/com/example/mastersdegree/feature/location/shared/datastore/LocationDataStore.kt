package com.example.mastersdegree.feature.location.shared.datastore

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mastersdegree.feature.location.shared.entity.LocationEntity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

// TODO: Похорошему сделать интерфейс
class LocationDataStore(activity: Activity) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

    private var lastKnownLocation: Location? = null
    var currentLocation: LocationEntity? by mutableStateOf(null)

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            if (lastLocation != null)
                updateLocation(lastLocation)
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun updateLocation(location: Location) {
        val distance = lastKnownLocation?.distanceTo(location) ?: Float.MAX_VALUE
        if (distance > 1) { // Обновление состояния, если изменение > 10 метров
            lastKnownLocation = location
            currentLocation = LocationEntity(
                longitude = location.longitude,
                latitude = location.latitude
            )
        }
    }
}
