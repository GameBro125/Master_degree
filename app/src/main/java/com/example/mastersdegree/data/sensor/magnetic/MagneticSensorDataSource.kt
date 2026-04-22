package com.example.mastersdegree.data.sensor.magnetic

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat
import com.example.mastersdegree.domain.model.MagneticField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MagneticSensorDataSource(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager? =
        ContextCompat.getSystemService(context, SensorManager::class.java)
    private val geomagneticSensor: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _magneticField = MutableStateFlow(MagneticField())
    val magneticField: StateFlow<MagneticField> = _magneticField.asStateFlow()

    init {
        sensorManager?.registerListener(
            this,
            geomagneticSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_MAGNETIC_FIELD) return

        val values = event.values
        _magneticField.value = MagneticField(
            x = values[0],
            y = values[1],
            z = values[2]
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
