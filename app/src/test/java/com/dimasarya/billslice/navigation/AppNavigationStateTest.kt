package com.dimasarya.billslice.navigation

import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class AppNavigationStateTest {
    @Test
    fun startupCompletesOnceAtHome() {
        val backStack = mutableListOf<NavKey>(SplashRoute)
        val state = AppNavigationState(backStack)

        state.completeStartup()
        state.completeStartup()

        assertEquals(listOf(HomeRoute), backStack)
    }

    @Test
    fun repeatedFocusedDestinationTapDoesNotDuplicate() {
        val backStack = mutableListOf<NavKey>(HomeRoute)
        val state = AppNavigationState(backStack)

        state.navigate(ScanReceiptRoute)
        state.navigate(ScanReceiptRoute)

        assertEquals(listOf(HomeRoute, ScanReceiptRoute), backStack)
    }

    @Test
    fun topLevelNavigationKeepsSingleDestination() {
        val backStack = mutableListOf<NavKey>(HomeRoute, ManualEntryRoute())
        val state = AppNavigationState(backStack)

        state.navigateTopLevel(SettingsRoute)
        state.navigateTopLevel(SettingsRoute)

        assertEquals(listOf(SettingsRoute), backStack)
    }

    @Test
    fun focusedFlowCanNavigateBackToHome() {
        val backStack = mutableListOf<NavKey>(HomeRoute, ManualEntryRoute())
        val state = AppNavigationState(backStack)

        assertTrue(state.navigateBack())
        assertEquals(HomeRoute, state.currentRoute)
        assertFalse(state.navigateBack())
    }

    @Test
    fun nonTopLevelRouteCannotUseTopLevelNavigation() {
        val state = AppNavigationState(mutableListOf<NavKey>(HomeRoute))

        assertThrows(IllegalArgumentException::class.java) {
            state.navigateTopLevel(ManualEntryRoute())
        }
    }
}
