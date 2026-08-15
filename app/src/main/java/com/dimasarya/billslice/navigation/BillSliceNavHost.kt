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
    ) -> Unit,
    val history: @Composable () -> Unit,
    val settings: @Composable (onLifetimePro: () -> Unit) -> Unit,
    val scanReceipt: @Composable (onBack: () -> Unit) -> Unit,
    val manualEntry: @Composable (onBack: () -> Unit) -> Unit,
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
                    { navigationState.navigate(ManualEntryRoute) },
                    { navigationState.navigateTopLevel(HistoryRoute) },
                    { navigationState.navigateTopLevel(SettingsRoute) },
                    { navigationState.navigate(LifetimeProRoute) },
                )
            }
            entry<HistoryRoute> { entries.history() }
            entry<SettingsRoute> {
                entries.settings { navigationState.navigate(LifetimeProRoute) }
            }
            entry<ScanReceiptRoute> {
                entries.scanReceipt { navigationState.navigateBack() }
            }
            entry<ManualEntryRoute> {
                entries.manualEntry { navigationState.navigateBack() }
            }
            entry<LifetimeProRoute> {
                entries.lifetimePro { navigationState.navigateBack() }
            }
        },
    )
}
