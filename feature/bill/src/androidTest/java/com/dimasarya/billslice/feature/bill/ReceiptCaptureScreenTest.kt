package com.dimasarya.billslice.feature.bill

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import com.dimasarya.billslice.core.model.ReceiptOcrOutcome
import org.junit.Rule
import org.junit.Test

class ReceiptCaptureScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun captureScreenKeepsLocalPrivacyAndManualFallbackVisible() {
        composeRule.setContent {
            BillSliceTheme {
                ReceiptCaptureScreen(
                    onBack = {},
                    onEnterManually = {},
                    onImageSelected = { ReceiptOcrOutcome.Success("sanitized") },
                )
            }
        }

        composeRule.onNodeWithText("Use camera").assertIsDisplayed()
        composeRule.onNodeWithText("Choose image").assertIsDisplayed()
        composeRule.onNodeWithText("Receipt images stay on your device.").assertIsDisplayed()
        composeRule.onNodeWithText("Enter items manually").assertIsDisplayed()
    }
}
