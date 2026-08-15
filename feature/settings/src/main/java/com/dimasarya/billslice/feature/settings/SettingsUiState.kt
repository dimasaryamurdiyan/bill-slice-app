package com.dimasarya.billslice.feature.settings

sealed interface QuotaSettingUiState {
    data class Available(
        val remaining: Int,
        val resetLabel: String,
    ) : QuotaSettingUiState

    data object Unavailable : QuotaSettingUiState
}

sealed interface ProSettingUiState {
    data object Available : ProSettingUiState
    data object Active : ProSettingUiState
    data object Unavailable : ProSettingUiState
}

data class BuildInfoUi(
    val versionName: String,
    val buildType: String,
)

data class SettingsUiState(
    val buildInfo: BuildInfoUi,
    val defaultCurrency: String = "IDR",
    val quota: QuotaSettingUiState = QuotaSettingUiState.Unavailable,
    val pro: ProSettingUiState = ProSettingUiState.Unavailable,
)
