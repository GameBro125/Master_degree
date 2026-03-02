package com.example.mastersdegree.feature.summaryUserInfo.view

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
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
fun MagneticFieldInfoScreen(
    mainViewModel: MainViewModel,
    locationViewModel: LocationViewModel
) {
    val state by mainViewModel.state.collectAsStateWithLifecycle()
    val locationState by locationViewModel.state.collectAsStateWithLifecycle()

    // Отправка данных
    val sendData = rememberUpdatedState(newValue = {
        mainViewModel.sendData()
    })

    // Запрос обновлений локации
    val requestLocationUpdates = rememberUpdatedState(newValue = {
        locationViewModel.requestLocationUpdates()
    })


    MagneticFieldInfoContent(
        magneticField = state.magneticField,
        userLocation = locationState.location,
        onButtonClick = sendData.value,
        requestLocationUpdates = requestLocationUpdates.value,
        activity = LocalContext.current as ComponentActivity
    )

    Text(
        modifier = Modifier.padding(top = 40.dp),
        text = "${mainViewModel.sendResult.collectAsStateWithLifecycle().value}"
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