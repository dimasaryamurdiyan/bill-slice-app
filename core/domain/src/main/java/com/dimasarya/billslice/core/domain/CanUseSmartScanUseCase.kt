package com.dimasarya.billslice.core.domain

import com.dimasarya.billslice.core.model.SmartScanAvailability
import com.dimasarya.billslice.core.model.SmartScanQuota

class CanUseSmartScanUseCase {
    operator fun invoke(quota: SmartScanQuota): SmartScanAvailability = SmartScanAvailability(quota)
}
