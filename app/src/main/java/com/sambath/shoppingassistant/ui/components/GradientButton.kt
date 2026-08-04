package com.sambath.shoppingassistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val PrimaryGradient = Brush.horizontalGradient(
    listOf(Color(0xFF1B6C63), Color(0xFF2A9D8F))
)

val AccentGradient = Brush.horizontalGradient(
    listOf(Color(0xFF415F91), Color(0xFF6B8BC3))
)

@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: Brush = PrimaryGradient,
    content: @Composable RowScope.() -> Unit
) {
    val pressState = rememberSpringPressState(restElevation = 8.dp, pressedElevation = 3.dp)
    val shape = RoundedCornerShape(14.dp)
    val alpha = if (enabled) 1f else 0.45f

    Box(
        modifier = modifier
            .springPress(state = pressState, enabled = enabled, onClick = onClick)
            .shadow(pressState.elevation(), shape, clip = false)
            .clip(shape)
            .background(gradient)
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides Color.White.copy(alpha = alpha)
        ) {
            ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}
