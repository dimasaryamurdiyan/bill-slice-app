package com.dimasarya.billslice.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimasarya.billslice.core.domain.ObserveRecentBillsUseCase
import com.dimasarya.billslice.core.model.RecentBillSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeViewModel(
    observeRecentBillsUseCase: ObserveRecentBillsUseCase,
    private val onRecentBillsFailure: (Throwable) -> Unit = {},
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = observeRecentBillsUseCase()
        .map { bills ->
            val recentUi = bills.take(MAX_HOME_RECENT_BILLS).map { it.toRecentBillUi() }
            HomeUiState(
                quota = SmartScanQuotaUiState.Unavailable,
                recentBills = if (recentUi.isEmpty()) {
                    RecentBillsUiState.Empty
                } else {
                    RecentBillsUiState.Populated(recentUi)
                },
                isOfflineReady = true,
            )
        }
        .catch { error ->
            onRecentBillsFailure(error)
            emit(HomeUiState(recentBills = RecentBillsUiState.Unavailable))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    companion object {
        const val MAX_HOME_RECENT_BILLS = 5
    }
}

private fun RecentBillSummary.toRecentBillUi(): RecentBillUi {
    val dateLabel = if (createdAtEpochMillis > 0) {
        formatRecentBillDate(createdAtEpochMillis)
    } else {
        null
    }
    return RecentBillUi(
        id = id,
        merchantName = merchantName,
        dateLabel = dateLabel,
        peopleCount = participantCount,
        totalLabel = total.format(),
    )
}

internal fun formatRecentBillDate(
    epochMillis: Long,
    locale: Locale = Locale.getDefault(),
    timeZone: TimeZone = TimeZone.getDefault(),
): String {
    return DateFormat.getDateInstance(DateFormat.MEDIUM, locale).apply {
        this.timeZone = timeZone
    }.format(Date(epochMillis))
}
