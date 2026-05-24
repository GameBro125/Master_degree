package com.example.mastersdegree.feature.summary.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mastersdegree.R
import com.example.mastersdegree.domain.model.Location
import com.example.mastersdegree.domain.service.StatusForegroundService
import com.example.mastersdegree.feature.summary.presentation.SendResult
import com.example.mastersdegree.feature.summary.presentation.SummaryIntent
import com.example.mastersdegree.feature.summary.presentation.SummaryState
import com.example.mastersdegree.feature.summary.presentation.SummaryViewModel
import com.example.mastersdegree.ui.theme.MastersDegreeTheme
import com.eygraber.compose.permissionx.PermissionStatus
import com.eygraber.compose.permissionx.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlin.math.abs

private enum class SummaryTab {
    Graphs, Monitor, Settings
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val notificationPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val backgroundLocationPermissionState =
        rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    var selectedTab by remember { mutableStateOf(SummaryTab.Graphs) }
    var sendIntervalInput by remember(state.sendIntervalSeconds) {
        mutableStateOf(state.sendIntervalSeconds.toString())
    }

    val context = LocalContext.current
    val appContext = context.applicationContext
    val statusForegroundService = Intent(context, StatusForegroundService::class.java)

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
            appContext.stopService(statusForegroundService)
        }
    }

    when (locationPermissionState.status) {
        PermissionStatus.NotGranted.Denied,
        PermissionStatus.NotGranted.NotRequested,
        PermissionStatus.NotGranted.PermanentlyDenied -> {
            LocationPermissionAlert(
                label = stringResource(R.string.locationPermissionAsk),
                onRequestPermission = {
                    if (locationPermissionState.status == PermissionStatus.NotGranted.PermanentlyDenied) {
                        locationPermissionState.openAppSettings()
                    } else {
                        locationPermissionState.launchPermissionRequest()
                    }
                },
                onDismissRequest = { }
            )
        }

        PermissionStatus.Granted -> {
            Scaffold(
                bottomBar = {
                    BottomAppBar {
                        NavigationBarItem(
                            selected = selectedTab == SummaryTab.Graphs,
                            onClick = { selectedTab = SummaryTab.Graphs },
                            icon = { Text("X/Y/Z") },
                            label = { Text("Data") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == SummaryTab.Monitor,
                            onClick = { selectedTab = SummaryTab.Monitor },
                            icon = { Text("ON") },
                            label = { Text("Monitor") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == SummaryTab.Settings,
                            onClick = { selectedTab = SummaryTab.Settings },
                            icon = { Text("15s") },
                            label = { Text("Settings") }
                        )
                    }
                }
            ) { innerPadding ->
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(320)) togetherWith
                            fadeOut(animationSpec = tween(220))
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    label = "tab-content"
                ) { tab ->
                    when (tab) {
                        SummaryTab.Graphs -> GraphsTabContent(
                            state = state,
                            permissionStatus = locationPermissionState.status,
                            onSendClick = { viewModel.onIntent(SummaryIntent.SendSummary) },
                            onRequestPermission = { locationPermissionState.launchPermissionRequest() },
                            onOpenAppSettings = { locationPermissionState.openAppSettings() }
                        )

                        SummaryTab.Monitor -> MonitorTabContent(
                            monitoringEnabled = state.monitoringEnabled,
                            onToggle = { enabled ->
                                if (enabled) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                        notificationPermissionState.status != PermissionStatus.Granted
                                    ) {
                                        notificationPermissionState.launchPermissionRequest()
                                        return@MonitorTabContent
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                                        backgroundLocationPermissionState.status != PermissionStatus.Granted
                                    ) {
                                        backgroundLocationPermissionState.launchPermissionRequest()
                                        return@MonitorTabContent
                                    }
                                    appContext.startForegroundService(statusForegroundService)
                                } else {
                                    appContext.stopService(statusForegroundService)
                                }
                                viewModel.onIntent(SummaryIntent.SetMonitoringEnabled(enabled))
                            }
                        )

                        SummaryTab.Settings -> SettingsTabContent(
                            value = sendIntervalInput,
                            onValueChange = { input ->
                                sendIntervalInput = input.filter { it.isDigit() }.take(4)
                            },
                            onApply = {
                                val parsed = sendIntervalInput.toIntOrNull() ?: 15
                                val safeSeconds = parsed.coerceAtLeast(1)
                                sendIntervalInput = safeSeconds.toString()
                                viewModel.onIntent(SummaryIntent.SetSendIntervalSeconds(safeSeconds))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GraphsTabContent(
    state: SummaryState,
    permissionStatus: PermissionStatus,
    onSendClick: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    val xHistory = remember { mutableStateListOf<Float>() }
    val yHistory = remember { mutableStateListOf<Float>() }
    val zHistory = remember { mutableStateListOf<Float>() }
    val bHistory = remember { mutableStateListOf<Float>() }

    LaunchedEffect(state.magneticField) {
        xHistory.pushPoint(state.magneticField.x)
        yHistory.pushPoint(state.magneticField.y)
        zHistory.pushPoint(state.magneticField.z)
        bHistory.pushPoint(state.magneticField.vector)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LocationBlock(location = state.location)
        MagneticGraphCard("X", xHistory, Color(0xFFD32F2F))
        MagneticGraphCard("Y", yHistory, Color(0xFF1976D2))
        MagneticGraphCard("Z", zHistory, Color(0xFF388E3C))
        MagneticGraphCard("|B|", bHistory, Color(0xFFF57C00))
        Spacer(modifier = Modifier.height(8.dp))
        SendButton(
            permissionStatus = permissionStatus,
            onGrantedClick = onSendClick,
            requestPermission = onRequestPermission,
            openAppSettings = onOpenAppSettings,
            isLoading = state.sendResult is SendResult.Loading
        )
        SendResultText(sendResult = state.sendResult)
    }
}

@Composable
private fun MonitorTabContent(
    monitoringEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (monitoringEnabled) "Monitoring is ON" else "Monitoring is OFF",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onToggle(!monitoringEnabled) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (monitoringEnabled) Color(0xFFC62828) else Color(0xFF2E7D32)
                )
            ) {
                Text(if (monitoringEnabled) "Disable monitoring" else "Enable monitoring")
            }
        }
    }
}

@Composable
private fun SettingsTabContent(
    value: String,
    onValueChange: (String) -> Unit,
    onApply: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.padding(20.dp)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("App settings", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text("Send interval (seconds)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Text("Default value: 15 sec", style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onApply, modifier = Modifier.fillMaxWidth()) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
private fun MagneticGraphCard(
    title: String,
    points: List<Float>,
    color: Color
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(points.lastOrNull()?.let { "%.2f".format(it) } ?: "--")
            }
            Spacer(modifier = Modifier.height(8.dp))
            LineChart(points = points, color = color, modifier = Modifier.fillMaxWidth().height(120.dp))
        }
    }
}

@Composable
private fun LineChart(
    points: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas
        val min = points.minOrNull() ?: return@Canvas
        val max = points.maxOrNull() ?: return@Canvas
        val range = (max - min).takeIf { abs(it) > 0.001f } ?: 1f
        val stepX = size.width / (points.size - 1)

        val path = Path()
        points.forEachIndexed { index, value ->
            val x = stepX * index
            val normalized = (value - min) / range
            val y = size.height - (normalized * size.height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawLine(
            color = color.copy(alpha = 0.2f),
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2f
        )
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}

private fun MutableList<Float>.pushPoint(value: Float) {
    add(value)
    if (size > 50) removeAt(0)
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
            fontSize = 20.sp
        )
        Text(
            text = "${stringResource(R.string.longitude)} ${location.longitude}",
            fontSize = 20.sp
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
    AlertDialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
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
                Spacer(Modifier.size(20.dp))
                Button(onClick = onRequestPermission) { Text(text = buttonText) }
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
private fun SendButton(
    permissionStatus: PermissionStatus,
    onGrantedClick: () -> Unit,
    requestPermission: () -> Unit,
    openAppSettings: () -> Unit,
    isLoading: Boolean,
) {
    when (permissionStatus) {
        PermissionStatus.Granted -> {
            Button(onClick = onGrantedClick, enabled = !isLoading, contentPadding = PaddingValues(12.dp)) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp).size(16.dp)
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

        PermissionStatus.NotGranted.PermanentlyDenied -> DisabledSendButton(onClick = openAppSettings)
        else -> DisabledSendButton(onClick = requestPermission)
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
