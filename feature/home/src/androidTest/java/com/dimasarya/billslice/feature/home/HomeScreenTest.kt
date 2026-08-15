package com.dimasarya.billslice.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyOfflineStateKeepsBothEntryActionsAvailable() {
        var action = ""
        composeRule.setContent {
            BillSliceTheme {
                HomeScreen(
                    state = HomeUiState(),
                    onScanReceipt = { action = "scan" },
                    onEnterManually = { action = "manual" },
                    onHistory = {},
                    onLifetimePro = {},
                )
            }
        }

        composeRule.onNodeWithText("Offline ready").assertIsDisplayed()
        composeRule.onNodeWithText("No recent bills yet").assertIsDisplayed()
        composeRule.onNodeWithText("Scan Receipt").performClick()
        assertEquals("scan", action)
        composeRule.onNodeWithText("Enter Manually").performClick()
        assertEquals("manual", action)
    }
}
