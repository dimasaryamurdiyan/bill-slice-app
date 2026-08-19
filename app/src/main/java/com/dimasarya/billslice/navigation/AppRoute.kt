package com.dimasarya.billslice.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey

@Serializable
data object SplashRoute : AppRoute

@Serializable
data object HomeRoute : AppRoute

@Serializable
data object HistoryRoute : AppRoute

@Serializable
data object SettingsRoute : AppRoute

@Serializable
data object ScanReceiptRoute : AppRoute

@Serializable
data class ManualEntryRoute(val billId: String? = null) : AppRoute

@Serializable
data object LifetimeProRoute : AppRoute

@Serializable
data object StartupErrorRoute : AppRoute

val AppRoute.isTopLevel: Boolean
    get() = this == HomeRoute || this == HistoryRoute || this == SettingsRoute
