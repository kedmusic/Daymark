package com.daymark.app.ui.screens.home

import androidx.lifecycle.ViewModel
import com.daymark.app.data.FakeData
import com.daymark.app.model.Category
import com.daymark.app.model.Person
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Clock
import java.time.LocalDate

data class HomeUiState(
    val selectedCategory: Category = Category.ALL,
    val events: List<Person> = emptyList(),
    val totalUpcomingMoments: Int = 0,
    val sharedMomentsCount: Int = 0,
    val snackbarMessage: String? = null
)

class HomeViewModel(
    private val clock: Clock = Clock.systemDefaultZone(),
    initialPeople: List<Person> = FakeData.samplePeople
) : ViewModel() {

    private val _people = MutableStateFlow(initialPeople)
    val people: StateFlow<List<Person>> = _people.asStateFlow()

    private val _selectedCategory = MutableStateFlow(Category.ALL)
    val selectedCategory: StateFlow<Category> = _selectedCategory.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun getToday(): LocalDate = LocalDate.now(clock)

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun setPeople(list: List<Person>) {
        _people.value = list
    }

    fun onEventClicked(person: Person) {
        _snackbarMessage.value = "Selected ${person.name}'s ${person.eventTitle}"
    }

    fun onQuickActionClicked(actionName: String, person: Person) {
        _snackbarMessage.value = "$actionName for ${person.name}"
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    /**
     * Helper to process events:
     * 1. Filter by category
     * 2. Distinct by person id (each person appears only once)
     * 3. Sort today's events first, then by soonest date
     */
    fun processEvents(rawList: List<Person>, category: Category, today: LocalDate): List<Person> {
        val filtered = if (category == Category.ALL) {
            rawList
        } else {
            rawList.filter { it.category == category }
        }

        val distinctPeople = filtered.distinctBy { it.id }

        return distinctPeople.sortedWith(
            compareBy<Person> { person ->
                val days = person.daysUntil(today)
                if (days == 0) -1 else days
            }
        )
    }

    fun getUiState(today: LocalDate = getToday()): HomeUiState {
        val currentCategory = _selectedCategory.value
        val rawPeople = _people.value
        val processed = processEvents(rawPeople, currentCategory, today)

        // Count shared moments: non-private events
        val sharedCount = processed.count { !it.isPrivate }

        return HomeUiState(
            selectedCategory = currentCategory,
            events = processed,
            totalUpcomingMoments = processed.size,
            sharedMomentsCount = sharedCount,
            snackbarMessage = _snackbarMessage.value
        )
    }
}
