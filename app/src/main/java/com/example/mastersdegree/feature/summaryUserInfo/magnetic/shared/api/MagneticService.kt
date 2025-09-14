package com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.api

import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.entity.MagneticFieldEntity
import com.example.mastersdegree.feature.summaryUserInfo.magnetic.shared.response.MagneticVectorResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MagneticService {
    @POST("magnetic-field")
    suspend fun sendMagneticField(
        @Body
        magneticField: MagneticFieldEntity
    ): Response<MagneticVectorResponse>
}