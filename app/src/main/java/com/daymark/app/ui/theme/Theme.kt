package com.daymark.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DaymarkLightColorScheme = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    primaryContainer = Lavender,
    onPrimaryContainer = Purple,
    secondary = Blue,
    tertiary = Coral,
    background = Canvas,
    surface = Color.White,
    onSurface = Ink,
    onSurfaceVariant = Muted,
    outline = Line
)

@Composable
fun DaymarkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DaymarkLightColorScheme,
        typography = DaymarkTypography,
        content = content
    )
}
