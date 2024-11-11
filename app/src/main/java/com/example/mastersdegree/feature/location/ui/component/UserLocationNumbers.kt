@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.mastersdegree.feature.location.ui.component

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.automirrored.sharp.Send
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mastersdegree.MainViewModel
import com.example.mastersdegree.MainViewModel.Companion.GPS_REQUEST_LOCATION
import com.example.mastersdegree.R
import com.example.mastersdegree.feature.location.shared.entity.LocationEntity
import com.example.mastersdegree.ui.theme.MastersDegreeTheme
import com.eygraber.compose.permissionx.PermissionStatus
import com.eygraber.compose.permissionx.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority


@Composable
fun UserLocationNumbers(
    modifier: Modifier = Modifier,
    locationEntity: LocationEntity?,
    requestLocationUpdates: () -> Unit,
) {

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    when (locationPermissionState.status) {

        PermissionStatus.Granted -> {
            requestLocationUpdates()
            if (locationEntity != null)
                UserLocationNumbersRoot(modifier, locationEntity)
        }

        PermissionStatus.NotGranted.NotRequested -> {
            NoLocationPermissionText(
                modifier = modifier,
                requestPermission = { locationPermissionState.launchPermissionRequest() }
            )
        }

        PermissionStatus.NotGranted.Denied ->
            NoLocationPermissionText(
                modifier = modifier,
                angryMode = true,
                requestPermission = { locationPermissionState.launchPermissionRequest() }
            )

        PermissionStatus.NotGranted.PermanentlyDenied -> {
            NoLocationPermissionText(
                modifier = modifier,
                superAngryMode = true,
                requestPermission = { locationPermissionState.openAppSettings() }
            )
        }
    }
}

@Composable
private fun UserLocationNumbersRoot(
    modifier: Modifier,
    locationEntity: LocationEntity,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.latitude) + locationEntity.latitude,
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = stringResource(R.string.longitude) + locationEntity.longitude,
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
    }
}

@Composable
fun SendButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    activity: Activity
) {
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    Row(modifier = modifier) {
        when (locationPermissionState.status) {
            PermissionStatus.Granted -> {
                Button(
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(),
                    onClick = onClick,
                    enabled = true
                ) {
                    Text(
                        text = stringResource(R.string.sendButton),
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.padding(horizontal = 8.dp))
                    Icon(
                        modifier = Modifier.padding(8.dp),
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = stringResource(R.string.sendButton)
                    )
                }
            }

            else -> {
                Button(
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    enabled = true,
                    onClick = {
                        if (locationPermissionState.status == PermissionStatus.NotGranted.Denied || locationPermissionState.status == PermissionStatus.NotGranted.NotRequested)
                            locationPermissionState.launchPermissionRequest()
                        if (locationPermissionState.status == PermissionStatus.NotGranted.PermanentlyDenied) locationPermissionState.openAppSettings()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.noSendPermissionButtonText),
                        fontSize = 16.sp
                    )
                    Icon(
                        modifier = Modifier.padding(8.dp),
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = stringResource(R.string.sendButton),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

fun showEnableLocationSetting(activity: Activity) {
    val locationRequest = LocationRequest.create()
    locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    val task = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build())
    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            try {
                exception.startResolutionForResult(activity, GPS_REQUEST_LOCATION)
            } catch (_: Exception) {

            }
        }
    }
}

@Preview
@Composable
private fun UserLocationNumbersPreview() {
    MastersDegreeTheme {
        Column {
            UserLocationNumbers(
                locationEntity = LocationEntity(longitude = 1.0, latitude = 1.0),
                requestLocationUpdates = { }
            )
            SendButton(onClick = {}, activity = Activity())
        }
    }
}

