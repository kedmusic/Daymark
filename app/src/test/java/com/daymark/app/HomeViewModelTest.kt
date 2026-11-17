package com.daymark.app

import com.daymark.app.model.Category
import com.daymark.app.model.Person
import com.daymark.app.ui.screens.home.HomeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HomeViewModelTest {

    private val fixedClock = Clock.fixed(
        Instant.parse("2026-05-15T10:00:00Z"),
        ZoneId.of("UTC")
    )
    private val today = LocalDate.now(fixedClock) // 2026-05-15

    @Test
    fun testDaysUntil_todayTomorrowRollsOverAndLeapYear() {
        // Today
        val pToday = Person("1", "P1", Category.FAMILY, "Bday", 5, 15)
        assertEquals(0, pToday.daysUntil(today))

        // Tomorrow
        val pTomorrow = Person("2", "P2", Category.FAMILY, "Bday", 5, 16)
        assertEquals(1, pTomorrow.daysUntil(today))

        // Earlier this year rolls to next year (2027-05-10)
        val pEarlier = Person("3", "P3", Category.FAMILY, "Bday", 5, 10)
        val expectedDays = java.time.temporal.ChronoUnit.DAYS.between(today, LocalDate.of(2027, 5, 10)).toInt()
        assertEquals(expectedDays, pEarlier.daysUntil(today))

        // Feb 29 in a non-leap year (2025 is non-leap)
        val nonLeapToday = LocalDate.of(2025, 2, 28)
        val pLeapDay = Person("4", "P4", Category.FAMILY, "Bday", 2, 29)
        assertEquals(0, pLeapDay.daysUntil(nonLeapToday))
    }

    @Test
    fun testSorting_todaysEventsSortedFirstThenSoonest() {
        val viewModel = HomeViewModel(fixedClock)
        val p1 = Person("1", "Alice", Category.FAMILY, "Event 1", 5, 20) // in 5 days
        val p2 = Person("2", "Bob", Category.FRIENDS, "Event 2", 5, 15)   // Today (0 days)
        val p3 = Person("3", "Charlie", Category.OFFICE, "Event 3", 5, 16) // in 1 day

        val sorted = viewModel.processEvents(listOf(p1, p2, p3), Category.ALL, today)

        assertEquals("Bob", sorted[0].name)     // Today first
        assertEquals("Charlie", sorted[1].name) // 1 day
        assertEquals("Alice", sorted[2].name)   // 5 days
    }

    @Test
    fun testFilterChips_returnCorrectPeople() {
        val pFamily = Person("1", "Alice", Category.FAMILY, "Bday", 5, 20)
        val pFriends = Person("2", "Bob", Category.FRIENDS, "Bday", 6, 1)
        val pOffice = Person("3", "Charlie", Category.OFFICE, "Bday", 7, 10)

        val viewModel = HomeViewModel(fixedClock, listOf(pFamily, pFriends, pOffice))

        assertEquals(3, viewModel.processEvents(listOf(pFamily, pFriends, pOffice), Category.ALL, today).size)

        val familyList = viewModel.processEvents(listOf(pFamily, pFriends, pOffice), Category.FAMILY, today)
        assertEquals(1, familyList.size)
        assertEquals("Alice", familyList[0].name)

        val friendsList = viewModel.processEvents(listOf(pFamily, pFriends, pOffice), Category.FRIENDS, today)
        assertEquals(1, friendsList.size)
        assertEquals("Bob", friendsList[0].name)

        val officeList = viewModel.processEvents(listOf(pFamily, pFriends, pOffice), Category.OFFICE, today)
        assertEquals(1, officeList.size)
        assertEquals("Charlie", officeList[0].name)
    }

    @Test
    fun testPersonAppearsOnlyOnce() {
        val viewModel = HomeViewModel(fixedClock)
        val p1 = Person("1", "Alice", Category.FAMILY, "Bday", 5, 20)
        val p1Duplicate = Person("1", "Alice", Category.FAMILY, "Anniversary", 10, 10)

        val processed = viewModel.processEvents(listOf(p1, p1Duplicate), Category.ALL, today)
        assertEquals(1, processed.size)
        assertEquals("1", processed[0].id)
    }

    @Test
    fun testPersonalEventsFlaggedPrivateAndNotCountedAsShared() {
        val pPublic = Person("1", "Alice", Category.FAMILY, "Bday", 5, 20, isPrivate = false)
        val pPrivate = Person("2", "Bob", Category.FRIENDS, "Secret", 6, 1, isPrivate = true)

        val viewModel = HomeViewModel(fixedClock, listOf(pPublic, pPrivate))
        val state = viewModel.getUiState(today)

        assertEquals(2, state.totalUpcomingMoments)
        assertEquals(1, state.sharedMomentsCount)
        assertTrue(pPrivate.isPrivate)
        assertFalse(pPublic.isPrivate)
    }

    @Test
    fun testPersonWithNoPhoneNumberProducesCompleteModel() {
        val pNoPhone = Person("1", "Alex", Category.OFFICE, "Bday", 7, 20, phoneNumber = null)
        assertNull(pNoPhone.phoneNumber)
        assertEquals("Alex", pNoPhone.name)
        assertEquals("AL", pNoPhone.avatarInitials)
    }

    @Test
    fun testUpcomingMomentsCountMatches() {
        val p1 = Person("1", "Alice", Category.FAMILY, "Bday", 5, 20)
        val p2 = Person("2", "Bob", Category.FRIENDS, "Bday", 6, 1)

        val viewModel = HomeViewModel(fixedClock, listOf(p1, p2))
        val state = viewModel.getUiState(today)

        assertEquals(2, state.totalUpcomingMoments)
    }

    @Test
    fun testEmptyListState() {
        val viewModel = HomeViewModel(fixedClock, emptyList())
        val state = viewModel.getUiState(today)

        assertEquals(0, state.totalUpcomingMoments)
        assertTrue(state.events.isEmpty())
    }
}
