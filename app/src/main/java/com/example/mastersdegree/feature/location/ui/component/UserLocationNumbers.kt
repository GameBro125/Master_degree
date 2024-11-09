package com.example.mastersdegree.feature.location.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mastersdegree.R
import com.example.mastersdegree.feature.location.shared.entity.LocationEntity
import com.example.mastersdegree.ui.theme.MastersDegreeTheme
import com.eygraber.compose.permissionx.PermissionStatus
import com.eygraber.compose.permissionx.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
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
    locationEntity: LocationEntity
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

@Preview
@Composable
private fun UserLocationNumbersPreview() {
    MastersDegreeTheme {
        UserLocationNumbers(
            locationEntity = LocationEntity(longitude = 1.0, latitude = 1.0),
            requestLocationUpdates = { }
        )
    }
}