package com.example.mastersdegree.domain.usecase

import com.example.mastersdegree.domain.model.SummaryPayload
import com.example.mastersdegree.domain.repository.SummaryRepository

class SendSummaryDataUseCase(
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(payload: SummaryPayload): Result<Unit> {
        return summaryRepository.send(payload)
    }
}
