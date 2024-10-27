package com.example.mastersdegree

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mastersdegree.domain.MagneticField
import com.example.mastersdegree.ui.theme.MastersDegreeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val geomagneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        setContent {
            var magneticField by remember {
                mutableStateOf(
                    MagneticField(
                        0.0F,
                        0.0F,
                        0.0F
                    )
                )
            } // Инициализация
            val sensorEventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        val geomagneticValues =
                            event.values // Массив значений магнитного поля по осям X, Y, Z
                        magneticField = magneticField.copy(
                            x = geomagneticValues[0],
                            y = geomagneticValues[1],
                            z = geomagneticValues[2]
                        )

                        // Дальнейшая обработка данных
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // Этот метод можно оставить пустым, если не нужно следить за точностью
                }
            }

            sensorManager.registerListener(
                sensorEventListener,
                geomagneticSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )

            MastersDegreeTheme {
                Surface(Modifier.fillMaxSize()) {
                    MagneticFieldInfo(magneticField)
                }
            }
        }

    }
}


@Composable
fun MagneticFieldInfo(magneticField: MagneticField) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
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
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MastersDegreeTheme {
//        Surface(Modifier.fillMaxSize()) {
//            MagneticFieldInfo(magneticField)
//        }
//    }
//}