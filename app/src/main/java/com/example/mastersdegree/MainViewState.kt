package com.example.mastersdegree

import com.example.mastersdegree.feature.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.magnetic.shared.entity.MagneticFieldEntity

// TODO: Make Presentation
data class MainViewState(
    val location: LocationEntity? = null,
    val magneticField: MagneticFieldEntity? = null,
)