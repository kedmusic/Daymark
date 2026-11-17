package com.daymark.app.model

import java.time.LocalDate
import java.time.Year
import java.time.temporal.ChronoUnit

enum class Category(val displayName: String) {
    ALL("All"),
    FAMILY("Family"),
    FRIENDS("Friends"),
    OFFICE("Office")
}

data class Person(
    val id: String,
    val name: String,
    val category: Category,
    val eventTitle: String,
    val eventMonth: Int, // 1 - 12
    val eventDay: Int,   // 1 - 31
    val phoneNumber: String? = null,
    val isPrivate: Boolean = false,
    val avatarInitials: String = name.take(2).uppercase()
) {
    /**
     * Calculates days until the next occurrence of this event relative to [today].
     * Handles leap years appropriately for Feb 29 events in non-leap years.
     */
    fun daysUntil(today: LocalDate): Int {
        val targetDateThisYear = createSafeDate(today.year, eventMonth, eventDay)
        val nextOccurrence = if (!targetDateThisYear.isBefore(today)) {
            targetDateThisYear
        } else {
            createSafeDate(today.year + 1, eventMonth, eventDay)
        }
        return ChronoUnit.DAYS.between(today, nextOccurrence).toInt()
    }

    companion object {
        fun createSafeDate(year: Int, month: Int, day: Int): LocalDate {
            val safeDay = if (month == 2 && day == 29 && !Year.isLeap(year.toLong())) {
                28
            } else {
                day
            }
            return LocalDate.of(year, month, safeDay)
        }
    }
}
