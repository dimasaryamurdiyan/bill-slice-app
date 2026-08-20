package com.dimasarya.billslice.core.model

data class SmartScanQuota(
    val remainingScans: Int,
    val resetsAtEpochMillis: Long,
) {
    init {
        require(remainingScans >= 0) { "Remaining scans cannot be negative" }
        require(resetsAtEpochMillis > 0) { "Quota reset time must be set" }
    }

    val isAvailable: Boolean
        get() = remainingScans > 0
}

data class SmartScanAvailability(
    val quota: SmartScanQuota,
) {
    val isAvailable: Boolean
        get() = quota.isAvailable
}
