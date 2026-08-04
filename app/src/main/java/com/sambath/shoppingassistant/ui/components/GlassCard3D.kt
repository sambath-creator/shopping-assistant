package com.sambath.shoppingassistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard3D(
    modifier: Modifier = Modifier,
    pressable: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val pressState = rememberSpringPressState(restElevation = 12.dp, pressedElevation = 4.dp)
    val shape = RoundedCornerShape(20.dp)
    val borderGradient = remember {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.85f),
                Color.White.copy(alpha = 0.35f),
                Color(0xFFE6F1ED).copy(alpha = 0.5f)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (pressable) {
                    Modifier.springPress(state = pressState, onClick = onClick)
                } else Modifier
            )
            .shadow(pressState.elevation(), shape, clip = false, ambientColor = Color(0x401B6C63), spotColor = Color(0x601B6C63))
            .clip(shape)
            .background(borderGradient)
            .padding(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(19.dp))
                .background(Color.White.copy(alpha = 0.78f))
                .padding(18.dp),
            content = content
        )
    }
}
