package com.dimasarya.billslice

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AppShellRestorationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun selectedTopLevelDestinationSurvivesActivityRecreation() {
        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithText("Receipt images stay on device").assertIsDisplayed()

        composeRule.activityRule.scenario.recreate()

        composeRule.onNodeWithText("Receipt images stay on device").assertIsDisplayed()
    }
}
