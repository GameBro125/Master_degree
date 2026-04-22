package com.example.mastersdegree

import com.example.mastersdegree.data.remote.RemoteDataEntity
import com.example.mastersdegree.feature.summaryUserInfo.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.entity.MagneticFieldEntity

data class MainViewState(
    val location: LocationEntity? = null,
    val magneticField: MagneticFieldEntity? = null,
    val data: RemoteDataEntity? = null
)