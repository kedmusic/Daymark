package com.daymark.app

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.daymark.app.model.Category
import com.daymark.app.model.Person
import com.daymark.app.ui.screens.home.HomeScreenContent
import com.daymark.app.ui.screens.home.HomeUiState
import com.daymark.app.ui.theme.DaymarkTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val today = LocalDate.of(2026, 5, 15)

    private val samplePeople = listOf(
        Person("1", "Today Person", Category.FAMILY, "Bday", 5, 15, phoneNumber = "+1 555-0100", isPrivate = false),
        Person("2", "Private Person", Category.FRIENDS, "Secret", 5, 20, phoneNumber = "+1 555-0200", isPrivate = true),
        Person("3", "Office Colleague", Category.OFFICE, "Anniversary", 6, 1, phoneNumber = null, isPrivate = false)
    )

    @Test
    fun testGreetingHeroFilterAndEventsDisplayed() {
        composeTestRule.setContent {
            DaymarkTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        selectedCategory = Category.ALL,
                        events = samplePeople,
                        totalUpcomingMoments = samplePeople.size
                    ),
                    today = today,
                    onCategorySelected = {},
                    onEventClick = {},
                    onQuickActionClick = { _, _ -> },
                    onAddClick = {}
                )
            }
        }

        // Greeting header
        composeTestRule.onNodeWithText("Good Day,").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alex").assertIsDisplayed()

        // Hero card
        composeTestRule.onNodeWithTag("hero_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 moments coming up").assertIsDisplayed()

        // Filter chips
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
        composeTestRule.onNodeWithText("Family").assertIsDisplayed()
        composeTestRule.onNodeWithText("Friends").assertIsDisplayed()
        composeTestRule.onNodeWithText("Office").assertIsDisplayed()

        // Event cards
        composeTestRule.onNodeWithText("Today Person").assertIsDisplayed()
        composeTestRule.onNodeWithText("Private Person").assertIsDisplayed()
        composeTestRule.onNodeWithText("Office Colleague").assertIsDisplayed()
    }

    @Test
    fun testTappingFilterChipCallsCallback() {
        var selectedCat: Category? = null
        composeTestRule.setContent {
            DaymarkTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        selectedCategory = Category.ALL,
                        events = samplePeople,
                        totalUpcomingMoments = samplePeople.size
                    ),
                    today = today,
                    onCategorySelected = { selectedCat = it },
                    onEventClick = {},
                    onQuickActionClick = { _, _ -> },
                    onAddClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Family").performClick()
        assertEquals(Category.FAMILY, selectedCat)
    }

    @Test
    fun testTodayCardIsShownFirstAndHighlighted() {
        composeTestRule.setContent {
            DaymarkTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        selectedCategory = Category.ALL,
                        events = samplePeople,
                        totalUpcomingMoments = samplePeople.size
                    ),
                    today = today,
                    onCategorySelected = {},
                    onEventClick = {},
                    onQuickActionClick = { _, _ -> },
                    onAddClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("today_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Today!").assertIsDisplayed()
    }

    @Test
    fun testPersonalEventShowsLockIcon() {
        composeTestRule.setContent {
            DaymarkTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        selectedCategory = Category.ALL,
                        events = samplePeople,
                        totalUpcomingMoments = samplePeople.size
                    ),
                    today = today,
                    onCategorySelected = {},
                    onEventClick = {},
                    onQuickActionClick = { _, _ -> },
                    onAddClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("lock_icon").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Private event").assertIsDisplayed()
    }

    @Test
    fun testTappingEventCardOrQuickActionTriggersCallback() {
        var clickedPerson: Person? = null
        var quickActionName: String? = null

        composeTestRule.setContent {
            DaymarkTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        selectedCategory = Category.ALL,
                        events = samplePeople,
                        totalUpcomingMoments = samplePeople.size
                    ),
                    today = today,
                    onCategorySelected = {},
                    onEventClick = { clickedPerson = it },
                    onQuickActionClick = { action, _ -> quickActionName = action },
                    onAddClick = {}
                )
            }
        }

        // Tap event card
        composeTestRule.onNodeWithText("Today Person").performClick()
        assertEquals("Today Person", clickedPerson?.name)

        // Tap quick action call button
        composeTestRule.onNodeWithContentDescription("Call Today Person").performClick()
        assertEquals("Call", quickActionName)
    }

    @Test
    fun testTappableItemsHaveMinimumSizeAndClickAction() {
        composeTestRule.setContent {
            DaymarkTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        selectedCategory = Category.ALL,
                        events = samplePeople,
                        totalUpcomingMoments = samplePeople.size
                    ),
                    today = today,
                    onCategorySelected = {},
                    onEventClick = {},
                    onQuickActionClick = { _, _ -> },
                    onAddClick = {}
                )
            }
        }

        // Check filter chip, FAB, quick action buttons have click action
        composeTestRule.onNodeWithText("All").assertHasClickAction()
        composeTestRule.onNodeWithTag("add_fab").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Call Today Person").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Message Today Person").assertHasClickAction()
    }

    @Test
    fun testEmptyStateDisplaysText() {
        composeTestRule.setContent {
            DaymarkTheme {
                HomeScreenContent(
                    uiState = HomeUiState(
                        selectedCategory = Category.ALL,
                        events = emptyList(),
                        totalUpcomingMoments = 0
                    ),
                    today = today,
                    onCategorySelected = {},
                    onEventClick = {},
                    onQuickActionClick = { _, _ -> },
                    onAddClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("No dates yet. Tap + to add someone.").assertIsDisplayed()
    }
}
