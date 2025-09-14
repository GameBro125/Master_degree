package com.example.mastersdegree.feature.summaryUserInfo.location.shared.api

import com.example.mastersdegree.feature.summaryUserInfo.location.shared.entity.LocationEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LocationService {
    @POST("location")
    suspend fun sendLocation(@Body location: LocationEntity): Response<LocationEntity>
}