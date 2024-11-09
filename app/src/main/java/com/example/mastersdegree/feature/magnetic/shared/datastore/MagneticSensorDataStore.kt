package com.example.mastersdegree.feature.magnetic.shared.datastore


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getSystemService
import com.example.mastersdegree.feature.magnetic.shared.entity.MagneticFieldEntity


// Добавить интерфейс
class MagneticSensorDataStore(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager? = getSystemService(context, SensorManager::class.java)
    private val geomagneticSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    init {
        sensorManager?.registerListener(
            this,
            geomagneticSensor,
            SensorManager.SENSOR_DELAY_NORMAL,
        )
    }

    var magneticField by mutableStateOf(MagneticFieldEntity())

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val geomagneticValues = event.values // Массив значений магнитного поля по осям X, Y, Z
            magneticField = magneticField.copy(
                x = geomagneticValues[0],
                y = geomagneticValues[1],
                z = geomagneticValues[2]
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}