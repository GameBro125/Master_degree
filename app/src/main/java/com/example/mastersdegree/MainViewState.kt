package com.example.mastersdegree

import com.example.mastersdegree.domain.magneticField.MagneticField

data class MainViewState(
    val locationState: Pair<Float, Float>? = null,
    val magneticFieldState: MagneticField? = null,
)