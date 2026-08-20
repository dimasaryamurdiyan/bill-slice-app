package com.dimasarya.billslice.core.model

data class SmartScanQuota(
    val remainingScans: Int?,
    val resetsAtEpochMillis: Long?,
    val plan: String = "free",
    val policy: String = "monthly_5",
    val usedScans: Int = 0,
    val limit: Int? = 5,
) {
    init {
        require(remainingScans == null || remainingScans >= 0) { "Remaining scans cannot be negative" }
        require(resetsAtEpochMillis == null || resetsAtEpochMillis > 0) { "Quota reset time must be positive" }
        require(plan.isNotBlank()) { "Quota plan cannot be blank" }
        require(policy.isNotBlank()) { "Quota policy cannot be blank" }
        require(usedScans >= 0) { "Used scans cannot be negative" }
        require(limit == null || limit >= 0) { "Quota limit cannot be negative" }
        require((limit == null) == (remainingScans == null)) {
            "Limit and remaining scans must both be present or both be absent"
        }
    }

    val isAvailable: Boolean
        get() = remainingScans?.let { it > 0 } ?: true
}

data class SmartScanAvailability(
    val quota: SmartScanQuota,
) {
    val isAvailable: Boolean
        get() = quota.isAvailable
}
