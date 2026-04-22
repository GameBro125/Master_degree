package com.example.mastersdegree.domain.repository

import com.example.mastersdegree.domain.model.SummaryPayload

interface SummaryRepository {
    suspend fun send(payload: SummaryPayload): Result<Unit>
}
