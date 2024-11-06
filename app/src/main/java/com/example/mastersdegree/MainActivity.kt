package com.example.mastersdegree

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.drawable.shapes.Shape
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.mastersdegree.domain.MagneticField
import com.example.mastersdegree.domain.MagneticSensorManager
import com.example.mastersdegree.domain.location.LocationManager
import com.example.mastersdegree.domain.remote.ApiService
import com.example.mastersdegree.domain.remote.MagneticFieldViewModel
import com.example.mastersdegree.ui.theme.MastersDegreeTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.roundToInt

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
    modifier: Modifier,
    magneticField: MagneticField,
    userLocation: Pair<Double, Double>,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        UserLocationNumbers(modifier = Modifier, userLocation = userLocation)
        MagneticFieldNumbers(modifier = Modifier, magneticField = magneticField)
        StateButton(modifier = Modifier, magneticField = magneticField)
    }
}

@Composable
fun UserLocationNumbers(modifier: Modifier, userLocation: Pair<Double, Double>) {
    Column (horizontalAlignment = Alignment.Start) {
        Text(
            text = "Latitude: " + userLocation.first,
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = "Longitude: " + userLocation.second,
            fontSize = 24.sp,
            fontWeight = FontWeight.W300
        )
    }
}

@Composable
fun MagneticFieldNumbers(modifier: Modifier, magneticField: MagneticField) {
    Column {
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
    modifier: Modifier, magneticField: MagneticField
) {
    Row() {
        Button(
            modifier = Modifier.padding(16.dp),
            onClick = {
                val viewModel: MagneticFieldViewModel = MagneticFieldViewModel()
                viewModel.sendMagneticFieldData(magneticField)
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
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
        {
            MagneticFieldInfo(
                magneticField = MagneticField(),
                modifier = Modifier,
                userLocation = 2.0 to 2.0
            )
        }
    }
}