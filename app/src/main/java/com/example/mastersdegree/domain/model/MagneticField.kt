package com.example.mastersdegree.domain.model

import kotlin.math.sqrt

data class MagneticField(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
) {
    val vector: Float
        get() = sqrt((x * x) + (y * y) + (z * z))
}
