package com.dimasarya.billslice

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.dimasarya.billslice.core.designsystem.theme.BillSliceThemeTokens
import com.dimasarya.billslice.core.designsystem.theme.DeepInk
import com.dimasarya.billslice.core.designsystem.theme.DeepEmerald
import com.dimasarya.billslice.core.designsystem.theme.MutedInk
import com.dimasarya.billslice.core.designsystem.theme.ReceiptMint
import com.dimasarya.billslice.core.designsystem.theme.SubtleBorder
import com.dimasarya.billslice.core.designsystem.theme.WarmCanvas
import com.dimasarya.billslice.core.designsystem.theme.WarmSurface
import com.dimasarya.billslice.feature.bill.BillFlowEntryMode
import com.dimasarya.billslice.feature.bill.BillFlowEntryScreen
import com.dimasarya.billslice.feature.home.HomeScreen
import com.dimasarya.billslice.feature.home.HomeUiState
import com.dimasarya.billslice.feature.settings.BuildInfoUi
import com.dimasarya.billslice.feature.settings.SettingsScreen
import com.dimasarya.billslice.feature.settings.SettingsUiState
import com.dimasarya.billslice.navigation.AppNavigationState
import com.dimasarya.billslice.navigation.AppRoute
import com.dimasarya.billslice.navigation.BillSliceFeatureEntries
import com.dimasarya.billslice.navigation.BillSliceNavHost
import com.dimasarya.billslice.navigation.HistoryRoute
import com.dimasarya.billslice.navigation.HomeRoute
import com.dimasarya.billslice.navigation.SettingsRoute
import com.dimasarya.billslice.navigation.isTopLevel
import com.dimasarya.billslice.navigation.rememberAppNavigationState

@Composable
fun BillSliceApp(
    initialStartupState: StartupUiState = StartupUiState.Ready,
) {
    val navigationState = rememberAppNavigationState()
    var startupState by remember { mutableStateOf(initialStartupState) }
    val entries = BillSliceFeatureEntries(
        splash = { onReady, onFailure ->
            SplashScreen(
                state = startupState,
                onReady = onReady,
                onFailure = onFailure,
            )
        },
        startupError = { onRetry ->
            StartupErrorScreen(
                recoverable = startupState != StartupUiState.UnrecoverableConfigurationFailure,
                onRetry = {
                    startupState = StartupUiState.Ready
                    onRetry()
                },
            )
        },
        home = { onScan, onManual, onHistory, _, onLifetimePro ->
            HomeScreen(
                state = HomeUiState(),
                onScanReceipt = onScan,
                onEnterManually = onManual,
                onHistory = onHistory,
                onLifetimePro = onLifetimePro,
            )
        },
        history = {
            PlaceholderScreen(
                title = R.string.history_placeholder_title,
                body = R.string.history_placeholder_body,
            )
        },
        settings = { onLifetimePro ->
            SettingsScreen(
                state = SettingsUiState(
                    buildInfo = BuildInfoUi(
                        versionName = BuildConfig.VERSION_NAME,
                        buildType = BuildConfig.BUILD_TYPE,
                    ),
                ),
                onLifetimePro = onLifetimePro,
            )
        },
        scanReceipt = { onBack ->
            BillFlowEntryScreen(BillFlowEntryMode.Scan, onBack = onBack)
        },
        manualEntry = { onBack ->
            BillFlowEntryScreen(BillFlowEntryMode.Manual, onBack = onBack)
        },
        lifetimePro = { onBack ->
            PlaceholderScreen(
                title = R.string.lifetime_pro_placeholder_title,
                body = R.string.lifetime_pro_placeholder_body,
                onBack = onBack,
            )
        },
    )

    AppNavigationShell(navigationState) {
        BillSliceNavHost(
            navigationState = navigationState,
            entries = entries,
        )
    }
}

@Composable
private fun AppNavigationShell(
    navigationState: AppNavigationState,
    content: @Composable () -> Unit,
) {
    if (!navigationState.currentRoute.isTopLevel) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            content()
        }
        return
    }

    val isExpanded = currentWindowAdaptiveInfo()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    if (!isExpanded) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = WarmCanvas,
            contentWindowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
            ),
            bottomBar = {
                BillSliceBottomNavigation(navigationState)
            },
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            ) {
                content()
            }
        }
        return
    }

    val navigationItems: NavigationSuiteScope.() -> Unit = {
        TopLevelDestination.entries.forEach { destination ->
            item(
                icon = {
                    Icon(
                        destination.icon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.label)) },
                selected = navigationState.currentRoute == destination.route,
                onClick = { navigationState.navigateTopLevel(destination.route) },
            )
        }
    }
    NavigationSuiteScaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        navigationSuiteItems = navigationItems,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            shortNavigationBarContainerColor = WarmSurface,
            shortNavigationBarContentColor = DeepInk,
            navigationBarContainerColor = WarmSurface,
            navigationBarContentColor = DeepInk,
            navigationRailContainerColor = WarmCanvas,
            navigationRailContentColor = DeepInk,
            navigationDrawerContainerColor = WarmSurface,
            navigationDrawerContentColor = DeepInk,
        ),
    ) {
        content()
    }
}

@Composable
private fun BillSliceBottomNavigation(navigationState: AppNavigationState) {
    Surface(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = BillSliceThemeTokens.spacing.large, vertical = 8.dp)
            .fillMaxWidth()
            .height(64.dp)
            .shadow(6.dp, MaterialTheme.shapes.extraLarge),
        color = WarmSurface,
        shape = MaterialTheme.shapes.extraLarge,
        border = androidx.compose.foundation.BorderStroke(1.dp, SubtleBorder),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TopLevelDestination.entries.forEach { destination ->
                val isSelected = navigationState.currentRoute == destination.route
                val contentColor = if (isSelected) DeepEmerald else MutedInk
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .selectable(
                            selected = isSelected,
                            role = Role.Tab,
                            onClick = { navigationState.navigateTopLevel(destination.route) },
                        )
                        .semantics { selected = isSelected },
                    color = if (isSelected) ReceiptMint else androidx.compose.ui.graphics.Color.Transparent,
                    contentColor = contentColor,
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = null,
                            modifier = Modifier.height(21.dp),
                        )
                        Text(
                            text = stringResource(destination.label),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}

private enum class TopLevelDestination(
    val route: AppRoute,
    @StringRes val label: Int,
    val icon: ImageVector,
) {
    Home(HomeRoute, R.string.nav_home, Icons.Rounded.Home),
    History(HistoryRoute, R.string.nav_history, Icons.Rounded.History),
    Settings(SettingsRoute, R.string.nav_settings, Icons.Rounded.Settings),
}

@Composable
private fun StartupErrorScreen(
    recoverable: Boolean,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(BillSliceThemeTokens.spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(
            BillSliceThemeTokens.spacing.large,
            Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(
                if (recoverable) {
                    R.string.startup_error_title
                } else {
                    R.string.startup_config_error_title
                },
            ),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = stringResource(
                if (recoverable) {
                    R.string.startup_error_body
                } else {
                    R.string.startup_config_error_body
                },
            ),
            style = MaterialTheme.typography.bodyLarge,
        )
        if (recoverable) {
            Button(onClick = onRetry) {
                Text(stringResource(R.string.startup_retry))
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    @StringRes title: Int,
    @StringRes body: Int,
    onBack: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(BillSliceThemeTokens.spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(
            BillSliceThemeTokens.spacing.large,
            Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(title),
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(text = stringResource(body), style = MaterialTheme.typography.bodyLarge)
        if (onBack != null) {
            Button(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                Text(stringResource(R.string.back))
            }
        }
    }
}
