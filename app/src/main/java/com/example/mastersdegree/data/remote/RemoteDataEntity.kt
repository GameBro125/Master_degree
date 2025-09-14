package com.example.mastersdegree.data.remote

import com.example.mastersdegree.feature.summaryUserInfo.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.entity.MagneticFieldEntity

data class RemoteDataEntity(
    val location: LocationEntity,
    val magneticField: MagneticFieldEntity
)
