package com.example.mastersdegree.domain.magneticField

import kotlin.math.sqrt

data class MagneticField(
    val x: Float = 0.0F,
    val y: Float = 0.0F,
    val z: Float = 0.0F,
) {
    fun getVector(): String {

        return sqrt(x*x + y*y + z*z).toString()

    }
}