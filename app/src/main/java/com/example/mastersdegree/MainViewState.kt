package com.example.mastersdegree

import com.example.mastersdegree.domain.magneticField.MagneticField

data class MainViewState(
    val location: Pair<Double, Double>? = null,
    val magneticField: MagneticField? = null,
)