package com.dimasarya.billslice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay

data class BillSliceFeatureEntries(
    val splash: @Composable (onReady: () -> Unit, onFailure: () -> Unit) -> Unit,
    val startupError: @Composable (onRetry: () -> Unit) -> Unit,
    val home: @Composable (
        onScanReceipt: () -> Unit,
        onEnterManually: () -> Unit,
        onHistory: () -> Unit,
        onSettings: () -> Unit,
        onLifetimePro: () -> Unit,
        onOpenBill: (String) -> Unit,
    ) -> Unit,
    val history: @Composable (
        onOpenBill: (String) -> Unit,
        onLifetimePro: () -> Unit,
    ) -> Unit,
    val settings: @Composable (onLifetimePro: () -> Unit) -> Unit,
    val scanReceipt: @Composable (onBack: () -> Unit, onEnterManually: () -> Unit) -> Unit,
    val manualEntry: @Composable (billId: String?, onBack: () -> Unit) -> Unit,
    val lifetimePro: @Composable (onBack: () -> Unit) -> Unit,
)

@Composable
fun BillSliceNavHost(
    navigationState: AppNavigationState,
    entries: BillSliceFeatureEntries,
) {
    NavDisplay(
        backStack = navigationState.backStack,
        onBack = { navigationState.navigateBack() },
        entryProvider = entryProvider {
            entry<SplashRoute> {
                entries.splash(
                    navigationState::completeStartup,
                    navigationState::showStartupError,
                )
            }
            entry<StartupErrorRoute> {
                entries.startupError { navigationState.navigate(SplashRoute) }
            }
            entry<HomeRoute> {
                entries.home(
                    { navigationState.navigate(ScanReceiptRoute) },
                    { navigationState.navigate(ManualEntryRoute()) },
                    { navigationState.navigateTopLevel(HistoryRoute) },
                    { navigationState.navigateTopLevel(SettingsRoute) },
                    { navigationState.navigate(LifetimeProRoute) },
                    { billId -> navigationState.navigate(ManualEntryRoute(billId)) },
                )
            }
            entry<HistoryRoute> {
                entries.history(
                    { billId -> navigationState.navigate(ManualEntryRoute(billId)) },
                    { navigationState.navigate(LifetimeProRoute) },
                )
            }
            entry<SettingsRoute> {
                entries.settings { navigationState.navigate(LifetimeProRoute) }
            }
            entry<ScanReceiptRoute> {
                entries.scanReceipt(
                    { navigationState.navigateBack() },
                    { navigationState.navigate(ManualEntryRoute()) },
                )
            }
            entry<ManualEntryRoute> { route ->
                entries.manualEntry(route.billId) { navigationState.navigateBack() }
            }
            entry<LifetimeProRoute> {
                entries.lifetimePro { navigationState.navigateBack() }
            }
        },
    )
}
