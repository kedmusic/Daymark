package com.daymark.app

import com.daymark.app.navigation.NavigationRoutes
import com.daymark.app.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationRouteTest {

    @Test
    fun testNavigationRoutesCountAndOrder() {
        assertEquals(5, NavigationRoutes.size)
        assertEquals("Home", NavigationRoutes[0].title)
        assertEquals("Calendar", NavigationRoutes[1].title)
        assertEquals("Groups", NavigationRoutes[2].title)
        assertEquals("Lists", NavigationRoutes[3].title)
        assertEquals("Profile", NavigationRoutes[4].title)
    }
}
