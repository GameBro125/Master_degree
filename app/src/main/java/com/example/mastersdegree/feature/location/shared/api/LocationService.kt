package com.example.mastersdegree.feature.location.shared.api

import com.example.mastersdegree.feature.location.shared.entity.LocationEntity
import com.example.mastersdegree.feature.magnetic.shared.entity.MagneticFieldEntity
import com.example.mastersdegree.feature.magnetic.shared.response.MagneticVectorResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LocationService {
    @POST("location")
    suspend fun sendLocation(@Body location: LocationEntity): Response<LocationEntity>
}