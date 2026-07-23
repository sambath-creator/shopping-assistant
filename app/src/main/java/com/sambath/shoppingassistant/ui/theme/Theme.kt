package com.sambath.shoppingassistant.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme = lightColorScheme(
    primary = Color(0xFF1B6C63),
    onPrimary = Color.White,
    secondary = Color(0xFF8A5A12),
    tertiary = Color(0xFF415F91),
    background = Color(0xFFF7FBFF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF14211F),
    surfaceVariant = Color(0xFFE0ECE9),
    outline = Color(0xFF71817D)
)

@Composable
fun ShoppingAssistantTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
