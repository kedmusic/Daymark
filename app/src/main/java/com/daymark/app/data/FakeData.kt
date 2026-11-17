package com.daymark.app.data

import com.daymark.app.model.Category
import com.daymark.app.model.Person

object FakeData {
    val samplePeople = listOf(
        Person(
            id = "1",
            name = "Sarah Jenkins",
            category = Category.FAMILY,
            eventTitle = "Birthday",
            eventMonth = 5,
            eventDay = 15,
            phoneNumber = "+1 555-0192",
            isPrivate = false
        ),
        Person(
            id = "2",
            name = "David Miller",
            category = Category.FRIENDS,
            eventTitle = "Work Anniversary",
            eventMonth = 6,
            eventDay = 1,
            phoneNumber = "+1 555-0143",
            isPrivate = true
        ),
        Person(
            id = "3",
            name = "Alex Rivera",
            category = Category.OFFICE,
            eventTitle = "Birthday",
            eventMonth = 7,
            eventDay = 20,
            phoneNumber = null,
            isPrivate = false
        ),
        Person(
            id = "4",
            name = "Mom",
            category = Category.FAMILY,
            eventTitle = "Mother's Day",
            eventMonth = 5,
            eventDay = 11,
            phoneNumber = "+1 555-0188",
            isPrivate = false
        )
    )
}
