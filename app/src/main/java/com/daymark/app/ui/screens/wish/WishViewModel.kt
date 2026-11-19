package com.daymark.app.ui.screens.wish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daymark.app.data.WishTemplates
import com.daymark.app.data.WishTone
import com.daymark.app.model.Person
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class WishUiState(
    val person: Person,
    val eventDate: String,
    val selectedTone: WishTone = WishTone.WARM,
    val message: String,
    val templateMessage: String = message,
    val isEditing: Boolean = false,
    val showReplaceEditsConfirmation: Boolean = false,
    val pendingTone: WishTone? = null,
    val showDiscardConfirmation: Boolean = false
) {
    val hasEdits: Boolean
        get() = message != templateMessage
}

class WishViewModel(
    person: Person,
    private val clock: Clock = Clock.systemDefaultZone()
) : ViewModel() {

    private val initialMessage = WishTemplates.message(person, WishTone.WARM)
    private val _uiState = MutableStateFlow(
        WishUiState(
            person = person,
            eventDate = getEventDate(person, LocalDate.now(clock)),
            message = initialMessage
        )
    )
    val uiState: StateFlow<WishUiState> = _uiState.asStateFlow()

    fun selectTone(tone: WishTone) {
        val state = _uiState.value
        if (tone == state.selectedTone) return

        if (state.hasEdits) {
            _uiState.value = state.copy(
                showReplaceEditsConfirmation = true,
                pendingTone = tone
            )
        } else {
            applyTone(tone)
        }
    }

    fun confirmToneReplacement() {
        val tone = _uiState.value.pendingTone
        if (tone != null) applyTone(tone)
        else dismissToneReplacement()
    }

    fun dismissToneReplacement() {
        _uiState.value = _uiState.value.copy(
            showReplaceEditsConfirmation = false,
            pendingTone = null
        )
    }

    fun updateMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }

    fun toggleEditing() {
        _uiState.value = _uiState.value.copy(isEditing = !_uiState.value.isEditing)
    }

    fun requestCancel(): Boolean {
        val state = _uiState.value
        if (state.hasEdits) {
            _uiState.value = state.copy(showDiscardConfirmation = true)
            return false
        }
        return true
    }

    fun dismissDiscardConfirmation() {
        _uiState.value = _uiState.value.copy(showDiscardConfirmation = false)
    }

    private fun applyTone(tone: WishTone) {
        val template = WishTemplates.message(_uiState.value.person, tone)
        _uiState.value = _uiState.value.copy(
            selectedTone = tone,
            message = template,
            templateMessage = template,
            showReplaceEditsConfirmation = false,
            pendingTone = null
        )
    }

    private fun getEventDate(person: Person, today: LocalDate): String {
        val dateThisYear = Person.createSafeDate(today.year, person.eventMonth, person.eventDay)
        val date = if (dateThisYear.isBefore(today)) {
            Person.createSafeDate(today.year + 1, person.eventMonth, person.eventDay)
        } else {
            dateThisYear
        }
        return date.format(DateTimeFormatter.ofPattern("MMM d"))
    }

    class Factory(
        private val person: Person,
        private val clock: Clock = Clock.systemDefaultZone()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(WishViewModel::class.java))
            return WishViewModel(person, clock) as T
        }
    }
}
