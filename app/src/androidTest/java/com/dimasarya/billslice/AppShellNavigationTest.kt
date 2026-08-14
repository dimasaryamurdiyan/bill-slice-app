package com.dimasarya.billslice

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.then
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import org.junit.Rule
import org.junit.Test

class AppShellNavigationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun manualEntryHidesTopLevelNavigationAndReturnsHome() {
        composeRule.setContent {
            BillSliceTheme { BillSliceApp() }
        }

        composeRule.onNodeWithText("Enter Manually").performClick()

        composeRule.onNodeWithText("Manual bill entry starts here and remains available without a network connection.")
            .assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Settings").assertDoesNotExist()
        composeRule.onNodeWithText("Back to Home").performClick()
        composeRule.onNodeWithText("Split bills, not friendships.").assertIsDisplayed()
    }

    @Test
    fun topLevelSettingsIsReachableFromHome() {
        composeRule.setContent {
            BillSliceTheme { BillSliceApp() }
        }

        composeRule.onNodeWithText("Settings").performClick()

        composeRule.onNodeWithText("Receipt images stay on device")
            .assertIsDisplayed()
    }

    @Test
    fun recoverableStartupFailureCanRetryToHome() {
        composeRule.setContent {
            BillSliceTheme {
                BillSliceApp(initialStartupState = StartupUiState.RecoverableFailure)
            }
        }

        composeRule.onNodeWithText("Try again").performClick()

        composeRule.onNodeWithText("Split bills, not friendships.").assertIsDisplayed()
    }

    @Test
    fun configurationFailureDoesNotOfferInvalidRetry() {
        composeRule.setContent {
            BillSliceTheme {
                BillSliceApp(
                    initialStartupState = StartupUiState.UnrecoverableConfigurationFailure,
                )
            }
        }

        composeRule.onNodeWithText("BillSlice needs a configuration fix").assertIsDisplayed()
        composeRule.onNodeWithText("Try again").assertDoesNotExist()
    }

    @Test
    fun largeFontCompactPhoneKeepsPrimaryActionsReachable() {
        composeRule.setContent {
            DeviceConfigurationOverride(
                DeviceConfigurationOverride.FontScale(2f) then
                    DeviceConfigurationOverride.ForcedSize(DpSize(360.dp, 640.dp)),
            ) {
                BillSliceTheme { BillSliceApp() }
            }
        }

        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Scan Receipt"))
        composeRule.onNodeWithText("Scan Receipt").assertIsDisplayed()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Enter Manually"))
        composeRule.onNodeWithText("Enter Manually").assertIsDisplayed()
    }

    @Test
    fun tabletKeepsTopLevelNavigationReachable() {
        composeRule.setContent {
            DeviceConfigurationOverride(
                DeviceConfigurationOverride.ForcedSize(DpSize(1280.dp, 800.dp)),
            ) {
                BillSliceTheme { BillSliceApp() }
            }
        }

        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithText("No account, no cloud sync. Bills remain on this device.")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithText("Home").performClick()
        composeRule.onNodeWithText("Scan Receipt").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun landscapePhoneKeepsBothPrimaryActionsReachable() {
        composeRule.setContent {
            DeviceConfigurationOverride(
                DeviceConfigurationOverride.ForcedSize(DpSize(800.dp, 400.dp)),
            ) {
                BillSliceTheme { BillSliceApp() }
            }
        }

        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Scan Receipt"))
        composeRule.onNodeWithText("Scan Receipt").assertIsDisplayed()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Enter Manually"))
        composeRule.onNodeWithText("Enter Manually").assertIsDisplayed()
    }
}
