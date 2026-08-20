package com.dimasarya.billslice.feature.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeUiStateTest {
    @Test
    fun defaultStateIsUsableOfflineWithoutOptionalData() {
        val state = HomeUiState()

        assertTrue(state.isOfflineReady)
        assertEquals(SmartScanQuotaUiState.Unavailable, state.quota)
        assertEquals(RecentBillsUiState.Loading, state.recentBills)
    }
}
