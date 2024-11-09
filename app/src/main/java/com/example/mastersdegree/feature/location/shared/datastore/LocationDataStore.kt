package com.example.mastersdegree.feature.location.shared.datastore

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import com.example.mastersdegree.feature.location.shared.entity.LocationEntity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

// TODO: Похорошему сделать интерфейс
class LocationDataStore(private val activity: Activity) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

    private var lastKnownLocation: Location? = null
    var currentLocation: LocationEntity? by mutableStateOf(null)

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30000 // Интервал обновления, например, каждые 30 секунд
        fastestInterval = 15000 // Быстрейший интервал обновления, например, каждые 15 секунд
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    init {
        permissionCheck {
            getLastLocation { location ->
                location?.let {
                    updateLocationState(it)
                }
            }
        }
        startLocationUpdates { location ->
            updateLocationState(location)
        }
    }

    private fun updateLocationState(location: Location) {
        val distance = lastKnownLocation?.distanceTo(location) ?: Float.MAX_VALUE
        if (distance > 1) { // Обновление состояния, если изменение > 10 метров
            lastKnownLocation = location
            currentLocation = LocationEntity(
                longitude = location.longitude,
                latitude = location.latitude
            )
        }
    }

    private fun permissionCheck(onPermissionGranted: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO Это должно быть не тут
            AlertDialog.Builder(activity)
                .setTitle("Location Permission Needed")
                .setMessage("This app needs the Location permission, please accept to use location functionality")
                .setPositiveButton("OK") { _, _ ->
                    requestLocationPermission()
                }
                .create()
                .show()
        } else {
            onPermissionGranted() // Разрешение уже есть, продолжаем
        }
    }

    private fun requestLocationPermission() {
        // TODO Это должно быть не тут
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getLastLocation(onLocationReceived: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    onLocationReceived(location)
                }
        } else {
            permissionCheck {
                getLastLocation(onLocationReceived)
            }
        }
    }

    private fun startLocationUpdates(onLocationUpdated: (Location) -> Unit) {
        permissionCheck {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        locationResult.lastLocation?.let(onLocationUpdated)
                    }
                }
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                )
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 99
    }
}