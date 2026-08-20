package com.dimasarya.billslice.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
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
                    state = HomeUiState(recentBills = RecentBillsUiState.Empty),
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

    @Test
    fun populatedStateShowsFiveBillsAndReopensSelectedBill() {
        var openedBillId = ""
        val bills = (1..5).map { index ->
            RecentBillUi(
                id = "bill-$index",
                merchantName = "Cafe $index",
                dateLabel = "13 Aug 2026",
                peopleCount = if (index == 1) 1 else index,
                totalLabel = "Rp${index}0.000",
            )
        }
        composeRule.setContent {
            BillSliceTheme {
                HomeScreen(
                    state = HomeUiState(recentBills = RecentBillsUiState.Populated(bills)),
                    onScanReceipt = {},
                    onEnterManually = {},
                    onHistory = {},
                    onLifetimePro = {},
                    onOpenBill = { openedBillId = it },
                )
            }
        }

        composeRule.onNodeWithText("Cafe 5").assertIsDisplayed()
        composeRule.onNodeWithText("13 Aug 2026 • 1 person").assertExists()
        val hasReopenLabel = SemanticsMatcher("has reopen label") { node ->
            node.config.getOrNull(SemanticsActions.OnClick)?.label == "Reopen or edit Cafe 1"
        }
        composeRule.onNode(hasText("Cafe 1") and hasClickAction() and hasReopenLabel).performClick()

        assertEquals("bill-1", openedBillId)
    }

    @Test
    fun unavailableRecentBillsDoesNotBlockEntryActions() {
        var manualEntryOpened = false
        composeRule.setContent {
            BillSliceTheme {
                HomeScreen(
                    state = HomeUiState(recentBills = RecentBillsUiState.Unavailable),
                    onScanReceipt = {},
                    onEnterManually = { manualEntryOpened = true },
                    onHistory = {},
                    onLifetimePro = {},
                )
            }
        }

        composeRule.onNodeWithText("Recent bills unavailable").assertIsDisplayed()
        composeRule.onNodeWithText("Enter Manually").performClick()
        assertEquals(true, manualEntryOpened)
    }
}
