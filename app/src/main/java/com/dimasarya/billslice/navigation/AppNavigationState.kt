package com.dimasarya.billslice.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

@Stable
class AppNavigationState(
    val backStack: MutableList<NavKey>,
) {
    val currentRoute: AppRoute
        get() = backStack.last() as AppRoute

    fun completeStartup() {
        replaceAll(HomeRoute)
    }

    fun showStartupError() {
        replaceAll(StartupErrorRoute)
    }

    fun navigate(route: AppRoute) {
        if (currentRoute != route) {
            backStack.add(route)
        }
    }

    fun navigateTopLevel(route: AppRoute) {
        require(route.isTopLevel) { "Top-level navigation requires a top-level route" }
        replaceAll(route)
    }

    fun navigateBack(): Boolean {
        if (backStack.size <= 1) return false
        backStack.removeAt(backStack.lastIndex)
        return true
    }

    private fun replaceAll(route: AppRoute) {
        if (backStack.size == 1 && currentRoute == route) return
        backStack.clear()
        backStack.add(route)
    }
}

@Composable
fun rememberAppNavigationState(): AppNavigationState {
    val backStack = rememberNavBackStack(SplashRoute)
    return remember(backStack) { AppNavigationState(backStack) }
}
