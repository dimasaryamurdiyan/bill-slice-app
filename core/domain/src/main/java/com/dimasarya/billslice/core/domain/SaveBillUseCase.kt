package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.BillCalculationResult
import com.dimasarya.billslice.core.model.BillDraft

class SaveBillUseCase(
    private val billRepository: BillRepository,
) {
    suspend operator fun invoke(draft: BillDraft, calculationResult: BillCalculationResult): Result<Unit> {
        return billRepository.saveBill(draft, calculationResult)
    }
}
