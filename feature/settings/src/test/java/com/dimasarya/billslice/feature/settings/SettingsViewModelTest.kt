package com.dimasarya.billslice.feature.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsViewModelTest {
    @Test
    fun unavailableOptionalStateDoesNotReplaceRequiredSettings() {
        val initial = SettingsUiState(
            buildInfo = BuildInfoUi("1.0", "debug"),
        )

        val state = SettingsViewModel(initial).uiState.value

        assertEquals("IDR", state.defaultCurrency)
        assertEquals(QuotaSettingUiState.Unavailable, state.quota)
        assertEquals(ProSettingUiState.Unavailable, state.pro)
        assertEquals(BuildInfoUi("1.0", "debug"), state.buildInfo)
    }
}
