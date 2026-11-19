package com.daymark.app.data

import com.daymark.app.model.Person

enum class WishTone(val label: String) {
    WARM("Warm"),
    FUNNY("Funny"),
    SHORT("Short"),
    FORMAL("Formal")
}

object WishTemplates {
    fun message(person: Person, tone: WishTone): String {
        val firstName = person.name.trim().split(Regex("\\s+")).firstOrNull().orEmpty()
        val event = when {
            person.eventTitle.contains("anniversary", ignoreCase = true) -> "anniversary"
            person.eventTitle.contains("birthday", ignoreCase = true) -> "birthday"
            else -> person.eventTitle
        }

        return when (tone) {
            WishTone.WARM ->
                "Happy $event, $firstName! Wishing you a day full of joy and lovely moments."
            WishTone.FUNNY ->
                "Happy $event, $firstName! You're making celebrating look easy. Hope it's a great one!"
            WishTone.SHORT ->
                "Happy $event, $firstName! Hope you have a wonderful day."
            WishTone.FORMAL ->
                "Wishing you a very happy $event, $firstName. May the year ahead bring you much happiness."
        }
    }
}
