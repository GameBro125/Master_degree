package com.example.mastersdegree.feature.magnetic.shared.entity

import kotlin.math.sqrt

data class MagneticFieldEntity(
    val x: Float = 0.0F,
    val y: Float = 0.0F,
    val z: Float = 0.0F,
) {
    fun getVector(): String {

        return sqrt(x*x + y*y + z*z).toString()

    }
}