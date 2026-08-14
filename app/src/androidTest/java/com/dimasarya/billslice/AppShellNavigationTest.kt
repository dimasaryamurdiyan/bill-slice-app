package com.dimasarya.billslice

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

        composeRule.onNodeWithContentDescription("Settings").performClick()

        composeRule.onNodeWithText("Receipt images stay on device")
            .assertIsDisplayed()
    }
}
