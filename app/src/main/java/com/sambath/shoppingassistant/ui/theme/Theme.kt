package com.sambath.shoppingassistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AppColorScheme = lightColorScheme(
    primary = Color(0xFF1B6C63),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF0D3D38),
    secondary = Color(0xFF8A5A12),
    onSecondary = Color.White,
    tertiary = Color(0xFF415F91),
    onTertiary = Color.White,
    background = Color(0xFFF7FBFF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF14211F),
    surfaceVariant = Color(0xFFE0ECE9),
    outline = Color(0xFF71817D)
)

private val AppTypography = androidx.compose.material3.Typography(
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)

@Composable
fun ShoppingAssistantTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
