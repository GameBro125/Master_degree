package com.example.mastersdegree.data.remote.api

import com.example.mastersdegree.data.remote.RemoteDataEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DataService {
    @POST("magnetic-field")
    suspend fun sendData(@Body location: RemoteDataEntity): Response<RemoteDataEntity>
}