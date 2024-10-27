package com.example.mastersdegree.domain

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.mastersdegree.MainActivity



class SensorInfo (private val context: Context) {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val geomagneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    fun getFieldInfo():MagneticField {
        return MagneticField(0.0F, 0.0F,0.0F)
    }
}

