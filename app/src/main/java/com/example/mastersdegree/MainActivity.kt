package com.example.mastersdegree

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.mastersdegree.feature.location.shared.datastore.LocationDataStore
import com.example.mastersdegree.feature.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.location.ui.component.SendButton
import com.example.mastersdegree.feature.location.ui.component.UserLocationNumbers
import com.example.mastersdegree.feature.location.ui.component.showEnableLocationSetting
import com.example.mastersdegree.feature.magnetic.shared.datastore.MagneticSensorDataStore
import com.example.mastersdegree.feature.magnetic.shared.entity.MagneticFieldEntity
import com.example.mastersdegree.feature.magnetic.ui.component.MagneticFieldNumbers
import com.example.mastersdegree.ui.theme.MastersDegreeTheme

class MainActivity : ComponentActivity() {

    private val locationDataStore by lazy { LocationDataStore(activity = this) }
    private val magneticSensorDataStore by lazy { MagneticSensorDataStore(context = this) }

    private val viewModelStoreOwner: ViewModelStoreOwner = this
    private val mainViewModel by lazy {
        ViewModelProvider.create(
            owner = viewModelStoreOwner,
            factory = MainViewModel.Factory,
            extras = MutableCreationExtras().apply {
                set(MainViewModel.LOCATION_MANAGER_KEY, locationDataStore)
                set(MainViewModel.MAGNETIC_SENSOR_MANAGER_KEY, magneticSensorDataStore)
            }
        )[MainViewModel::class]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MastersDegreeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val state by mainViewModel.state.collectAsStateWithLifecycle()
                    val sendData by remember {
                        mutableStateOf({
                            mainViewModel.sendData(this)
                        })
                    }
                    val requestLocationUpdates by remember {
                        mutableStateOf({ locationDataStore.requestLocationUpdates() })
                    }
                    MainField(
                        magneticField = state.magneticField,
                        userLocation = state.location,
                        onButtonClick = sendData,
                        requestLocationUpdates = requestLocationUpdates,
                        activity = this
                    )
                }
            }
        }
    }
}

@Composable
fun MainField(
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
        UserLocationNumbers(
            modifier = Modifier.padding(bottom = 32.dp),
            locationEntity = userLocation,
            requestLocationUpdates = requestLocationUpdates,
        )

        if (magneticField != null)
            MagneticFieldNumbers(
                modifier = Modifier.padding(bottom = 20.dp),
                magneticField = magneticField
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
            MainField(
                magneticField = MagneticFieldEntity(),
                userLocation = LocationEntity(longitude = 1.0, latitude = 1.0),
                requestLocationUpdates = {},
                onButtonClick = {},
                activity = MainActivity(),
            )
        }
    }
}