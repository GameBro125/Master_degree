package com.example.mastersdegree.data.repository

import com.example.mastersdegree.data.remote.api.SummaryApi
import com.example.mastersdegree.data.remote.dto.toRemoteDto
import com.example.mastersdegree.domain.model.SummaryPayload
import com.example.mastersdegree.domain.repository.SummaryRepository

class SummaryRepositoryImpl(
    private val summaryApi: SummaryApi
) : SummaryRepository {
    override suspend fun send(payload: SummaryPayload): Result<Unit> {
        return runCatching {
            val response = summaryApi.sendSummary(payload.toRemoteDto())
            if (!response.isSuccessful) {
                error("Server error: ${response.code()}")
            }
        }
    }
}
