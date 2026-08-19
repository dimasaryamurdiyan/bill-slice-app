package com.dimasarya.billslice.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimasarya.billslice.core.domain.ObserveRecentBillsUseCase
import com.dimasarya.billslice.core.model.RecentBillSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(
    observeRecentBillsUseCase: ObserveRecentBillsUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = observeRecentBillsUseCase()
        .map { bills ->
            val recentUi = bills.take(MAX_HOME_RECENT_BILLS).map { it.toRecentBillUi() }
            HomeUiState(
                quota = SmartScanQuotaUiState.Unavailable,
                recentBills = recentUi,
                isOfflineReady = true,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    companion object {
        const val MAX_HOME_RECENT_BILLS = 3
    }
}

private fun RecentBillSummary.toRecentBillUi(): RecentBillUi {
    val dateLabel = if (createdAtEpochMillis > 0) {
        val formatter = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault())
        formatter.format(Date(createdAtEpochMillis))
    } else {
        "Recent"
    }
    return RecentBillUi(
        id = id,
        merchantName = merchantName,
        dateLabel = dateLabel,
        peopleCount = participantCount,
        totalLabel = total.format(),
    )
}
