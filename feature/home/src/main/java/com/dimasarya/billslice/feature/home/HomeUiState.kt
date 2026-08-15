package com.dimasarya.billslice.feature.home

sealed interface SmartScanQuotaUiState {
    data class Available(val remaining: Int) : SmartScanQuotaUiState
    data object Unavailable : SmartScanQuotaUiState
}

data class RecentBillUi(
    val id: String,
    val merchantName: String,
    val dateLabel: String,
    val peopleCount: Int,
    val totalLabel: String,
)

data class HomeUiState(
    val quota: SmartScanQuotaUiState = SmartScanQuotaUiState.Unavailable,
    val recentBills: List<RecentBillUi> = emptyList(),
    val isOfflineReady: Boolean = true,
)
