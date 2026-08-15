package com.dimasarya.billslice.feature.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dimasarya.billslice.core.designsystem.theme.BillSliceTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun unavailableOptionalStateStillShowsRequiredSettingsAndProEntry() {
        var openedPro = false
        composeRule.setContent {
            BillSliceTheme {
                SettingsScreen(
                    state = SettingsUiState(
                        buildInfo = BuildInfoUi("1.0", "debug"),
                    ),
                    onLifetimePro = { openedPro = true },
                )
            }
        }

        composeRule.onNodeWithText("IDR").assertIsDisplayed()
        composeRule.onNodeWithText("Unavailable • Manual entry still works").assertIsDisplayed()
        composeRule.onNodeWithText("Receipt images stay on device").assertIsDisplayed()
        composeRule.onNodeWithText("Version 1.0 • debug").assertIsDisplayed()
        composeRule.onNodeWithText("Upgrade or restore").performClick()
        assertTrue(openedPro)
    }

    @Test
    fun availableQuotaAndProOfferRenderTheirLiveValues() {
        composeRule.setContent {
            BillSliceTheme {
                SettingsScreen(
                    state = SettingsUiState(
                        buildInfo = BuildInfoUi("1.0", "debug"),
                        quota = QuotaSettingUiState.Available(5, "1 Sep"),
                        pro = ProSettingUiState.Available,
                    ),
                    onLifetimePro = {},
                )
            }
        }

        composeRule.onNodeWithText("5 left • Resets 1 Sep").assertIsDisplayed()
        composeRule.onNodeWithText("Lifetime Pro • Rp79k").assertIsDisplayed()
    }

    @Test
    fun activeProStateRendersWithoutHidingOtherSettings() {
        composeRule.setContent {
            BillSliceTheme {
                SettingsScreen(
                    state = SettingsUiState(
                        buildInfo = BuildInfoUi("1.0", "debug"),
                        pro = ProSettingUiState.Active,
                    ),
                    onLifetimePro = {},
                )
            }
        }

        composeRule.onNodeWithText("Lifetime Pro active").assertIsDisplayed()
        composeRule.onNodeWithText("Thanks for supporting BillSlice").assertIsDisplayed()
        composeRule.onNodeWithText("IDR").assertIsDisplayed()
    }
}
