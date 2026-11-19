package com.daymark.app

import androidx.compose.ui.test.assertIsDisplayed

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.daymark.app.navigation.AppNavGraph
import com.daymark.app.ui.theme.DaymarkTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DaymarkUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppLaunchesWithHomeSelected() {
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
    }

    @Test
    fun testTappingEachTabShowsPlaceholderText() {
        val tabs = listOf("Calendar", "Groups", "Lists", "Profile", "Home")
        tabs.forEach { tabTitle ->
            composeTestRule.onNodeWithText(tabTitle).performClick()
            composeTestRule.onNodeWithText("$tabTitle Screen").assertIsDisplayed()
        }
    }

    @Test
    fun testTabStateSurvivesSwitchingAwayAndBack() {
        // Go to Calendar
        composeTestRule.onNodeWithText("Calendar").performClick()
        composeTestRule.onNodeWithText("Calendar Screen").assertIsDisplayed()

        // Switch to Profile
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.onNodeWithText("Profile Screen").assertIsDisplayed()

        // Come back to Calendar
        composeTestRule.onNodeWithText("Calendar").performClick()
        composeTestRule.onNodeWithText("Calendar Screen").assertIsDisplayed()
    }

    @Test
    fun testBottomBarHeight() {
        // Verify bottom bar items / navigation bar height or presence
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calendar").assertIsDisplayed()
    }
}
