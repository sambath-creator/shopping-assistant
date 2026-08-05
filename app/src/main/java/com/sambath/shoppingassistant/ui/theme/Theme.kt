package com.sambath.shoppingassistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sambath.shoppingassistant.R

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

private val OpenSansFontFamily = FontFamily(
    Font(R.font.opensans_regular, FontWeight.Normal),
    Font(R.font.opensans_bold, FontWeight.SemiBold),
    Font(R.font.opensans_bold, FontWeight.Bold),
    Font(R.font.opensans_bold, FontWeight.Black)
)

val defaultTypography = androidx.compose.material3.Typography()

private val AppTypography = androidx.compose.material3.Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = OpenSansFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = OpenSansFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = OpenSansFontFamily),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = OpenSansFontFamily),
    headlineMedium = TextStyle(
        fontFamily = OpenSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = OpenSansFontFamily),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = OpenSansFontFamily),
    titleMedium = TextStyle(
        fontFamily = OpenSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = OpenSansFontFamily),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = OpenSansFontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = OpenSansFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = OpenSansFontFamily),
    labelLarge = TextStyle(
        fontFamily = OpenSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = OpenSansFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = OpenSansFontFamily)
)

@Composable
fun ShoppingAssistantTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
