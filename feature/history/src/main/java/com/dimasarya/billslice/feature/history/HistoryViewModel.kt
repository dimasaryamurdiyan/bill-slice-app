package com.dimasarya.billslice.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimasarya.billslice.core.domain.EntitlementRepository
import com.dimasarya.billslice.core.domain.EntitlementStatus
import com.dimasarya.billslice.core.domain.ObserveRecentBillsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    observeRecentBillsUseCase: ObserveRecentBillsUseCase,
    entitlementRepository: EntitlementRepository? = null,
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = combine(
        observeRecentBillsUseCase(),
        entitlementRepository?.observeEntitlement() ?: flowOf(EntitlementStatus.FREE),
    ) { bills, entitlement ->
        val isPro = entitlement == EntitlementStatus.PRO
        if (bills.isEmpty()) {
            HistoryUiState.Empty
        } else {
            HistoryUiState.Populated(
                bills = bills,
                isPro = isPro,
                hasOlderBills = !isPro && bills.size >= ObserveRecentBillsUseCase.FREE_HISTORY_LIMIT,
            )
        }
    }
        .catch { emit(HistoryUiState.Error(it.message ?: "Failed to load history")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState.Loading,
        )
}
