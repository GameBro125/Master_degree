package com.example.mastersdegree

import com.example.mastersdegree.data.remote.RemoteDataEntity
import com.example.mastersdegree.feature.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.magnetic.shared.entity.MagneticFieldEntity

// TODO: Make Presentation // теперь тут ещё одна сущность ?: kringe
data class MainViewState(
    val location: LocationEntity? = null,
    val magneticField: MagneticFieldEntity? = null,
    val data: RemoteDataEntity? = null
)