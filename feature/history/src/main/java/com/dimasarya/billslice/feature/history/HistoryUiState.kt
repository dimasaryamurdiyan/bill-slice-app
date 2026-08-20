package com.dimasarya.billslice.feature.history

import com.dimasarya.billslice.core.model.RecentBillSummary

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Populated(
        val bills: List<RecentBillSummary>,
        val isPro: Boolean = false,
        val hasOlderBills: Boolean = false,
    ) : HistoryUiState
    data object Empty : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}
