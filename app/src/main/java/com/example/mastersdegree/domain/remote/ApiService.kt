package com.example.mastersdegree.domain.remote

import com.example.mastersdegree.domain.magneticField.MagneticField
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("magnetic-field")
    suspend fun sendMagneticField(@Body magneticField: MagneticField): Response<VectorResponse>
}

fun createApiService(): ApiService {
    val retrofit = RetrofitManagers()
    return retrofit.body.create(ApiService::class.java)
}