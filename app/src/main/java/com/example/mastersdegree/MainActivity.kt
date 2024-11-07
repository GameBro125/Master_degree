package com.example.mastersdegree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mastersdegree.domain.magneticField.MagneticField
import com.example.mastersdegree.domain.magneticField.MagneticSensorManager
import com.example.mastersdegree.feature.location.LocationManager
import com.example.mastersdegree.domain.remote.MagneticFieldViewModel
import com.example.mastersdegree.ui.theme.MastersDegreeTheme

class MainActivity : ComponentActivity() {
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val magneticSensorManager = MagneticSensorManager(context = this)
        locationManager = LocationManager(this)
        enableEdgeToEdge()

        setContent {
            MastersDegreeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val magneticField by remember { magneticSensorManager.magneticField }
                    val userLocationState by locationManager.currentUserLocation // Используем observable state
                    MagneticFieldInfo(
                        magneticField = magneticField,
                        modifier = Modifier,
                        userLocation = userLocationState // Передаем userLocationState вместо locationManager.userLocation
                    )
                }
            }
        }
    }
}

@Composable
fun MagneticFieldInfo(
    modifier: Modifier = Modifier,
    magneticField: MagneticField,
    userLocation: Pair<Double, Double>,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        UserLocationNumbers(userLocation = userLocation)
        MagneticFieldNumbers(magneticField = magneticField)
        StateButton(magneticField = magneticField)
    }
}

@Composable
fun UserLocationNumbers(
    modifier: Modifier = Modifier,
    userLocation: Pair<Double, Double>
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.latitude) + userLocation.first,
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = stringResource(R.string.longitude) + userLocation.second,
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
    }
}

@Composable
fun MagneticFieldNumbers(
    modifier: Modifier = Modifier,
    magneticField: MagneticField
) {
    Column(modifier = modifier) {
        Text(
            text = "X: " + magneticField.x,
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = "Y: " + magneticField.y,
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = "Z: " + magneticField.z,
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = "|B|: " + magneticField.getVector(),
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
    }
}

@Composable
fun StateButton(
    modifier: Modifier = Modifier,
    magneticField: MagneticField
) {
    val context = LocalContext.current
    Row(modifier) {
        Button(
            modifier = Modifier.padding(16.dp),
            onClick = {
                // TODO: создание VM вынести выше и
                //  в эту функцию передавать в параметрах метод sendMagneticFieldData
                val viewModel = MagneticFieldViewModel()
                viewModel.sendMagneticFieldData(magneticField, context)
            }
        ) {
            Text(
                text = stringResource(R.string.sendButton),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                contentDescription = stringResource(R.string.sendButton)
            )
        }
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
            MagneticFieldInfo(
                magneticField = MagneticField(),
                userLocation = 2.0 to 2.0,
            )
        }
    }
}