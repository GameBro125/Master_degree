package com.example.mastersdegree.data.remote.api

import com.example.mastersdegree.data.remote.dto.SummaryRemoteDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SummaryApi {
    @POST("/magnetic-field")
    suspend fun sendSummary(@Body payload: SummaryRemoteDto): Response<Unit>
}
