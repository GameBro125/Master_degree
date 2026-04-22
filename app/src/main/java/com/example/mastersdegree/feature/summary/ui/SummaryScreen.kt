package com.example.mastersdegree.feature.summary.ui

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mastersdegree.R
import com.example.mastersdegree.domain.model.Location
import com.example.mastersdegree.domain.model.MagneticField
import com.example.mastersdegree.feature.summary.presentation.SendResult
import com.example.mastersdegree.feature.summary.presentation.SummaryIntent
import com.example.mastersdegree.feature.summary.presentation.SummaryViewModel
import com.example.mastersdegree.ui.theme.MastersDegreeTheme
import com.eygraber.compose.permissionx.PermissionStatus
import com.eygraber.compose.permissionx.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status == PermissionStatus.Granted) {
            viewModel.onIntent(SummaryIntent.RequestLocationUpdates)
        } else {
            viewModel.onIntent(SummaryIntent.StopLocationUpdates)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onIntent(SummaryIntent.StopLocationUpdates)
        }
    }
    when (locationPermissionState.status) {
        PermissionStatus.NotGranted.Denied -> LocationPermissionAlert(
            label = stringResource(R.string.locationPermissionAsk),
            onRequestPermission = { locationPermissionState.launchPermissionRequest() },
            onDismissRequest = { }
        )

        PermissionStatus.NotGranted.NotRequested -> LocationPermissionAlert(
            label = stringResource(R.string.locationPermissionAsk),
            onRequestPermission = { locationPermissionState.launchPermissionRequest() },
            onDismissRequest = { }
        )

        PermissionStatus.NotGranted.PermanentlyDenied -> LocationPermissionAlert(
            label = stringResource(R.string.locationPermissionAsk),
            onRequestPermission = { locationPermissionState.openAppSettings() },
            onDismissRequest = { }
        )

        PermissionStatus.Granted ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LocationBlock(
                    location = state.location,
                )
                Spacer(modifier = Modifier.padding(top = 16.dp))
                MagneticBlock(magneticField = state.magneticField)
                Spacer(modifier = Modifier.padding(top = 20.dp))
                SendButton(
                    permissionStatus = locationPermissionState.status,
                    onGrantedClick = { viewModel.onIntent(SummaryIntent.SendSummary) },
                    requestPermission = { locationPermissionState.launchPermissionRequest() },
                    openAppSettings = { locationPermissionState.openAppSettings() },
                    isLoading = state.sendResult is SendResult.Loading
                )
                Spacer(modifier = Modifier.padding(top = 24.dp))
                SendResultText(sendResult = state.sendResult)
            }
    }

}

@Composable
private fun LocationBlock(
    location: Location?,
) {
    if (location == null) {
        Text(text = "Waiting for location...", fontSize = 18.sp)
    } else {
        Text(
            text = "${stringResource(R.string.latitude)} ${location.latitude}",
            fontSize = 22.sp
        )
        Text(
            text = "${stringResource(R.string.longitude)} ${location.longitude}",
            fontSize = 22.sp
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPermissionAlert(
    onRequestPermission: () -> Unit,
    onDismissRequest: () -> Unit,
    label: String,
    buttonText: String = stringResource(R.string.grant_permission)
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(Modifier.fillMaxSize(0.4F)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Button(
                    onClick = onRequestPermission
                ) {
                    Text(text = buttonText)
                }
            }
        }
    }
}

@Preview
@Composable
private fun LocationPermissionAlertPreview() {
    MastersDegreeTheme {
        Surface(Modifier.fillMaxSize()) {
            LocationPermissionAlert(
                onRequestPermission = { },
                onDismissRequest = { },
                label = stringResource(R.string.locationPermissionAsk),
                buttonText = "give permission"
            )
        }
    }
}

@Composable
private fun MagneticBlock(magneticField: MagneticField) {
    Text(text = "X: ${magneticField.x}", fontSize = 22.sp)
    Text(text = "Y: ${magneticField.y}", fontSize = 22.sp)
    Text(text = "Z: ${magneticField.z}", fontSize = 22.sp)
    Text(text = "|B|: ${magneticField.vector}", fontSize = 22.sp)
}

@Composable
private fun SendButton(
    permissionStatus: PermissionStatus,
    onGrantedClick: () -> Unit,
    requestPermission: () -> Unit,
    openAppSettings: () -> Unit,
    isLoading: Boolean,
) {
    when (permissionStatus) {
        PermissionStatus.Granted -> {
            Button(onClick = onGrantedClick, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(16.dp)
                    )
                }
                Text(text = stringResource(R.string.sendButton))
                Icon(
                    modifier = Modifier.padding(start = 8.dp),
                    imageVector = Icons.AutoMirrored.Outlined.Send,
                    contentDescription = stringResource(R.string.sendButton)
                )
            }
        }

        PermissionStatus.NotGranted.PermanentlyDenied -> {
            DisabledSendButton(onClick = openAppSettings)
        }

        else -> {
            DisabledSendButton(onClick = requestPermission)
        }
    }
}

@Composable
private fun DisabledSendButton(onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Gray),
        onClick = onClick
    ) {
        Text(text = stringResource(R.string.noSendPermissionButtonText))
        Icon(
            modifier = Modifier.padding(start = 8.dp),
            imageVector = Icons.Default.LocationOn,
            contentDescription = stringResource(R.string.noSendPermissionButtonText),
            tint = Color.DarkGray
        )
    }
}

@Composable
private fun SendResultText(sendResult: SendResult) {
    when (sendResult) {
        SendResult.Idle -> Unit
        SendResult.Loading -> Text(text = "Sending...")
        SendResult.Success -> Text(text = stringResource(R.string.data_sent))
        is SendResult.Error -> Text(text = sendResult.message)
    }
}

@Composable
private fun PermissionText(onClick: () -> Unit) {
    Text(
        text = stringResource(R.string.locationPermissionAsk),
        fontSize = 20.sp,
        modifier = Modifier.padding(8.dp)
    )
    Button(onClick = onClick) {
        Text(text = "Grant permission")
    }
}
