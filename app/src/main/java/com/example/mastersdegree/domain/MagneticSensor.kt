package com.example.mastersdegree.domain

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.compose.runtime.mutableStateOf

class MagneticSensor : SensorEventListener {

    val magneticField = mutableStateOf(MagneticField(0.0F, 0.0F, 0.0F)) // Инициализация


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val geomagneticValues = event.values // Массив значений магнитного поля по осям X, Y, Z
            magneticField.value = magneticField.value
                .copy(x = geomagneticValues[0], y = geomagneticValues[1], z = geomagneticValues[2])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}