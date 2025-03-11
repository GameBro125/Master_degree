package com.example.mastersdegree.feature.magnetic.shared.datastore

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import com.example.mastersdegree.feature.magnetic.shared.entity.MagneticFieldEntity

class MagneticFieldProcessor {
    private val gravityValues = FloatArray(3)
    private val geomagneticValues = FloatArray(3)

    fun updateSensorData(event: SensorEvent): MagneticFieldEntity? {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                gravityValues[0] = event.values[0]
                gravityValues[1] = event.values[1]
                gravityValues[2] = event.values[2]
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                geomagneticValues[0] = event.values[0]
                geomagneticValues[1] = event.values[1]
                geomagneticValues[2] = event.values[2]
            }
            else -> return null
        }

        // button(onClick -> {} ) {TODO{"круто"} }

        val rotationMatrix = FloatArray(9)
        val inclinationMatrix = FloatArray(9)

        return if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, gravityValues, geomagneticValues)) {
            val remappedMatrix = FloatArray(9)
            SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_X,  // Ось X остаётся той же
                SensorManager.AXIS_Z,  // Ось Z направлена вверх
                remappedMatrix
            )

            val correctedValues = FloatArray(3)
            SensorManager.getOrientation(remappedMatrix, correctedValues)

            MagneticFieldEntity(
                x = correctedValues[0],  // Корректированные значения
                y = correctedValues[1],
                z = correctedValues[2]
            )
        } else {
            null
        }
    }
}
