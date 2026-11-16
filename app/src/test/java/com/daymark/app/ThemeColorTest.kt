package com.daymark.app

import androidx.compose.ui.graphics.Color
import com.daymark.app.ui.theme.*
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeColorTest {

    @Test
    fun testThemeColors() {
        assertEquals(Color(0xFF5B3CC4), Purple)
        assertEquals(Color(0xFFF2EDFF), Lavender)
        assertEquals(Color(0xFFFF806D), Coral)
        assertEquals(Color(0xFF4C78D7), Blue)
        assertEquals(Color(0xFF3D9A7A), Green)
        assertEquals(Color(0xFF242136), Ink)
        assertEquals(Color(0xFF777386), Muted)
        assertEquals(Color(0xFFE9E6EF), Line)
        assertEquals(Color(0xFFF8F7FB), Canvas)
    }
}
