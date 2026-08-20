package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.BillDraft

class GetBillUseCase(
    private val billRepository: BillRepository,
) {
    suspend operator fun invoke(id: String): Result<BillDraft> {
        return billRepository.getBill(id)
    }
}
