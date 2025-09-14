package com.example.mastersdegree.feature.summaryUserInfo.view

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.example.mastersdegree.MainActivity
import com.example.mastersdegree.MainViewModel
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.summaryUserInfo.location.ui.LocationSummaryFields
import com.example.mastersdegree.feature.summaryUserInfo.location.ui.SendButton
import com.example.mastersdegree.feature.summaryUserInfo.location.viewModel.LocationViewModel
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.entity.MagneticFieldEntity
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.ui.component.MagneticFieldSummaryFields
import com.example.mastersdegree.ui.theme.MastersDegreeTheme


@Composable
fun MainActivity.MagneticFieldInfoScreen() {

    val viewModelStoreOwner: ViewModelStoreOwner = this

    val mainViewModel by lazy {
        ViewModelProvider.create(
            owner = viewModelStoreOwner,
            factory = MainViewModel.Factory,
            extras = MutableCreationExtras().apply {
                set(MainViewModel.LOCATION_MANAGER_KEY, locationDataStore)
                set(MainViewModel.MAGNETIC_SENSOR_MANAGER_KEY, magneticSensorDataStore)
            }
        )[MainViewModel::class]
    }

    val locationViewModel by lazy {
        ViewModelProvider.create(
            owner = viewModelStoreOwner,
            factory = LocationViewModel.Factory,
            extras = MutableCreationExtras().apply {
                set(LocationViewModel.LOCATION_MANAGER_KEY, locationDataStore)
            }
        )[MainViewModel::class]
    }

    val state by mainViewModel.state.collectAsStateWithLifecycle()

    val sendData by remember {
        mutableStateOf({
            mainViewModel.sendData(this)
        })
    }
    val requestLocationUpdates by remember {
        mutableStateOf({ locationDataStore.requestLocationUpdates() })
    }
    MagneticFieldInfoContent(
        magneticField = state.magneticField,
        userLocation = locationViewModel.state.collectAsStateWithLifecycle().value.location,
        onButtonClick = sendData,
        requestLocationUpdates = requestLocationUpdates,
        activity = this
    )
}

@Composable
fun MagneticFieldInfoContent(
    modifier: Modifier = Modifier,
    magneticField: MagneticFieldEntity?,
    userLocation: LocationEntity?,
    requestLocationUpdates: () -> Unit,
    onButtonClick: () -> Unit,
    activity: Activity
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LocationSummaryFields(
            modifier = Modifier.padding(bottom = 32.dp),
            locationEntity = userLocation,
            requestLocationUpdates = requestLocationUpdates,
        )
        MagneticFieldSummaryFields(
            modifier = Modifier.padding(bottom = 20.dp),
            magneticField = magneticField ?: MagneticFieldEntity()
        )
        SendButton(
            onClick = onButtonClick,
            activity = activity
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MastersDegreeTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MagneticFieldInfoContent(
                magneticField = MagneticFieldEntity(),
                userLocation = LocationEntity(longitude = 1.0, latitude = 1.0),
                requestLocationUpdates = {},
                onButtonClick = {},
                activity = MainActivity(),
            )
        }
    }
}