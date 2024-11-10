package com.example.mastersdegree.data.remote

import com.example.mastersdegree.feature.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.magnetic.shared.entity.MagneticFieldEntity

data class RemoteDataEntity(
    val location: LocationEntity,
    val magneticField: MagneticFieldEntity
)
