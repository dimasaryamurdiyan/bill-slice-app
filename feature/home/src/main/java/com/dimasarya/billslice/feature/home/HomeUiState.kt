package com.dimasarya.billslice.feature.home

sealed interface SmartScanQuotaUiState {
    data class Available(val remaining: Int) : SmartScanQuotaUiState
    data object Unavailable : SmartScanQuotaUiState
}

data class RecentBillUi(
    val id: String,
    val merchantName: String,
    val dateLabel: String?,
    val peopleCount: Int,
    val totalLabel: String,
)

sealed interface RecentBillsUiState {
    data object Loading : RecentBillsUiState
    data object Empty : RecentBillsUiState
    data class Populated(val bills: List<RecentBillUi>) : RecentBillsUiState
    data object Unavailable : RecentBillsUiState
}

data class HomeUiState(
    val quota: SmartScanQuotaUiState = SmartScanQuotaUiState.Unavailable,
    val recentBills: RecentBillsUiState = RecentBillsUiState.Loading,
    val isOfflineReady: Boolean = true,
)
