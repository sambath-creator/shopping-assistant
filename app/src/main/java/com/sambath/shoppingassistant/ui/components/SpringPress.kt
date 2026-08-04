package com.sambath.shoppingassistant.ui.components

import android.view.View
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

private const val PRESSED_SCALE = 0.96f
private const val REST_SCALE = 1f

@Composable
fun rememberSpringPressState(
    restElevation: Dp = 6.dp,
    pressedElevation: Dp = 2.dp
): SpringPressState {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(REST_SCALE) }
    var elevation by remember { mutableFloatStateOf(restElevation.value) }

    val springHost = remember { View(context) }

    val scaleXAnim = remember {
        SpringAnimation(springHost, DynamicAnimation.SCALE_X, REST_SCALE).apply {
            spring = SpringForce(REST_SCALE).apply {
                dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
                stiffness = SpringForce.STIFFNESS_MEDIUM
            }
            addUpdateListener { _, value, _ -> scale = value }
        }
    }
    val scaleYAnim = remember {
        SpringAnimation(springHost, DynamicAnimation.SCALE_Y, REST_SCALE).apply {
            spring = SpringForce(REST_SCALE).apply {
                dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
                stiffness = SpringForce.STIFFNESS_MEDIUM
            }
        }
    }

    DisposableEffect(scaleXAnim, scaleYAnim) {
        onDispose {
            scaleXAnim.cancel()
            scaleYAnim.cancel()
        }
    }

    fun animateTo(target: Float, targetElevation: Float) {
        scaleXAnim.animateToFinalPosition(target)
        scaleYAnim.animateToFinalPosition(target)
        elevation = targetElevation
    }

    return remember {
        SpringPressState(
            scale = { scale },
            elevation = { elevation.dp },
            onPress = { animateTo(PRESSED_SCALE, pressedElevation.value) },
            onRelease = { animateTo(REST_SCALE, restElevation.value) }
        )
    }
}

class SpringPressState internal constructor(
    val scale: () -> Float,
    val elevation: () -> Dp,
    internal val onPress: () -> Unit,
    internal val onRelease: () -> Unit
)

fun Modifier.springPress(
    state: SpringPressState,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    graphicsLayer {
        scaleX = state.scale()
        scaleY = state.scale()
    }.pointerInput(enabled, onClick) {
        if (!enabled) return@pointerInput
        detectTapGestures(
            onPress = {
                state.onPress()
                val released = tryAwaitRelease()
                state.onRelease()
                if (released && onClick != null) onClick()
            }
        )
    }
}
