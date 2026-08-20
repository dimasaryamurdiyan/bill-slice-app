package com.dimasarya.billslice.feature.history

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import com.dimasarya.billslice.core.model.CurrencyCode
import com.dimasarya.billslice.core.model.Money
import com.dimasarya.billslice.core.model.RecentBillSummary
import org.junit.Rule
import org.junit.Test

class HistoryScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun populatedHistoryDoesNotShowEmptyState() {
        composeRule.setContent {
            BillSliceTheme {
                HistoryScreen(
                    state = HistoryUiState.Populated(
                        bills = listOf(sampleBill),
                    ),
                    onOpenBill = {},
                    onLifetimePro = {},
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("Warung Sore").assertIsDisplayed()
        composeRule.onNodeWithText("1 person", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Empty history").assertDoesNotExist()
    }

    @Test
    fun proHistoryDoesNotClaimTheListIsLimitedToFiveBills() {
        composeRule.setContent {
            BillSliceTheme {
                HistoryScreen(
                    state = HistoryUiState.Populated(
                        bills = listOf(sampleBill),
                        isPro = true,
                    ),
                    onOpenBill = {},
                    onLifetimePro = {},
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("Your bills are stored locally on this device.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Your last 5 bills are stored locally on this device.")
            .assertDoesNotExist()
    }

    @Test
    fun emptyHistoryShowsEmptyStateWithoutBillRows() {
        composeRule.setContent {
            BillSliceTheme {
                HistoryScreen(
                    state = HistoryUiState.Empty,
                    onOpenBill = {},
                    onLifetimePro = {},
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("Empty history").assertIsDisplayed()
        composeRule.onNodeWithText("Warung Sore").assertDoesNotExist()
    }

    private companion object {
        val sampleBill = RecentBillSummary(
            id = "bill-1",
            merchantName = "Warung Sore",
            createdAtEpochMillis = 1_723_484_800_000,
            total = Money.idr(219_450),
            participantCount = 1,
            currency = CurrencyCode.IDR,
        )
    }
}
