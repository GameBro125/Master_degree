package com.example.mastersdegree.data.remote.dto

import com.example.mastersdegree.domain.model.Location
import com.example.mastersdegree.domain.model.MagneticField
import com.example.mastersdegree.domain.model.SummaryPayload

data class SummaryRemoteDto(
    val location: LocationRemoteDto,
    val magneticField: MagneticFieldRemoteDto,
)

data class LocationRemoteDto(
    val longitude: Double,
    val latitude: Double,
)

data class MagneticFieldRemoteDto(
    val x: Float,
    val y: Float,
    val z: Float,
)

fun SummaryPayload.toRemoteDto(): SummaryRemoteDto {
    return SummaryRemoteDto(
        location = LocationRemoteDto(
            longitude = location.longitude,
            latitude = location.latitude
        ),
        magneticField = MagneticFieldRemoteDto(
            x = magneticField.x,
            y = magneticField.y,
            z = magneticField.z
        )
    )
}

fun LocationRemoteDto.toDomain(): Location {
    return Location(
        longitude = longitude,
        latitude = latitude
    )
}

fun MagneticFieldRemoteDto.toDomain(): MagneticField {
    return MagneticField(
        x = x,
        y = y,
        z = z
    )
}
