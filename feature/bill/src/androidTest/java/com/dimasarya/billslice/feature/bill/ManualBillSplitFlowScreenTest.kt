package com.dimasarya.billslice.feature.bill

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import com.dimasarya.billslice.core.testing.CanonicalBillFixtures
import org.junit.Rule
import org.junit.Test

class ManualBillSplitFlowScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun canonicalBillPreloadedDraftFlowsFromManualEntryToSplitResultAndShare() {
        val canonicalDraft = CanonicalBillFixtures.createCanonicalDraft()

        composeRule.setContent {
            BillSliceTheme {
                ManualBillSplitFlowScreen(
                    initialDraft = canonicalDraft,
                    onBack = {},
                )
            }
        }

        // 1. Enter manually screen is displayed with canonical items
        composeRule.onNodeWithText("Enter manually").assertIsDisplayed()
        composeRule.onNodeWithText("Nasi Goreng").assertIsDisplayed()
        composeRule.onNodeWithText("Chicken Steak").assertIsDisplayed()
        composeRule.onNodeWithText("Pizza").assertIsDisplayed()
        composeRule.onNodeWithText("Rp40.000").assertIsDisplayed()

        // Click Save and Continue
        composeRule.onNodeWithText("Save and Continue").performClick()

        // 2. Add people screen
        composeRule.onNodeWithText("Add people").assertIsDisplayed()
        composeRule.onNodeWithText("Dimas").assertIsDisplayed()
        composeRule.onNodeWithText("Arya").assertIsDisplayed()
        composeRule.onNodeWithText("Budi").assertIsDisplayed()

        // Continue to Assign Items
        composeRule.onNodeWithText("Continue").performClick()

        // 3. Assign items screen
        composeRule.onNodeWithText("Assign items").assertIsDisplayed()
        composeRule.onNodeWithText("3 of 3 items assigned").assertIsDisplayed()

        // Continue to Calculate
        composeRule.onNodeWithText("Continue to Calculate").performClick()

        // 4. Calculation screen
        composeRule.onNodeWithText("Calculation").assertIsDisplayed()
        composeRule.onNodeWithText("Rp219.450").assertIsDisplayed()
        composeRule.onNodeWithText("Looks good! Matches receipt total.").assertIsDisplayed()

        // Calculate Split
        composeRule.onNodeWithText("Calculate Split").performClick()

        // 5. Your split screen
        composeRule.onNodeWithText("Your split").assertIsDisplayed()
        composeRule.onNodeWithText("Rp46.200").assertIsDisplayed()
        composeRule.onNodeWithText("Rp69.300").assertIsDisplayed()
        composeRule.onNodeWithText("Rp103.950").assertIsDisplayed()

        // Share to WhatsApp
        composeRule.onNodeWithText("Share to WhatsApp").performClick()

        // 6. Share preview screen
        composeRule.onNodeWithText("Share preview").assertIsDisplayed()
        composeRule.onNodeWithText("Share Now").assertIsDisplayed()
        composeRule.onNodeWithText("Copy Text").assertIsDisplayed()
    }
}
