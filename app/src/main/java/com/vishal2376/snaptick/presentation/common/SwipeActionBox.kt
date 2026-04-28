package com.vishal2376.snaptick.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.ui.theme.Blue500
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Red
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

enum class SwipeBehavior {
	NONE,
	DELETE,
	COMPLETE
}

private const val ANIMATION_DURATION_MS = 280

/**
 * Finger-following swipe row, gmail-style.
 *
 * Drag horizontally to reveal a tinted background with an icon that scales
 * up as the swipe crosses a threshold (30% of row width). Crossing the
 * threshold fires a light haptic. Release before threshold = spring back.
 * Release after threshold = animate fully off-screen, fire the callback,
 * then collapse the row with a fade-out.
 *
 * Same public API as the previous SwipeToDismiss-based implementation so
 * call sites don't change.
 */
@Composable
fun <T> SwipeActionBox(
	item: T,
	onDelete: (T) -> Unit,
	onComplete: (T) -> Unit,
	swipeBehavior: SwipeBehavior = SwipeBehavior.DELETE,
	iconTint: Color = Blue500,
	content: @Composable (T) -> Unit,
) {
	if (swipeBehavior == SwipeBehavior.NONE) {
		content(item)
		return
	}

	val haptic = LocalHapticFeedback.current
	val scope = rememberCoroutineScope()

	val offsetX = remember { Animatable(0f) }
	var rowWidthPx by remember { mutableStateOf(0f) }
	var hasCrossedThreshold by remember { mutableStateOf(false) }
	var isActionDone by remember { mutableStateOf(false) }

	val bgColor = when (swipeBehavior) {
		SwipeBehavior.DELETE -> Red
		SwipeBehavior.COMPLETE -> LightGreen
		else -> Color.Transparent
	}
	val icon = when (swipeBehavior) {
		SwipeBehavior.DELETE -> Icons.Default.Delete
		SwipeBehavior.COMPLETE -> Icons.Default.CheckCircleOutline
		else -> Icons.Default.Refresh
	}

	LaunchedEffect(isActionDone) {
		if (!isActionDone) return@LaunchedEffect
		delay(ANIMATION_DURATION_MS.toLong())
		when (swipeBehavior) {
			SwipeBehavior.DELETE -> onDelete(item)
			SwipeBehavior.COMPLETE -> onComplete(item)
			else -> {}
		}
	}

	AnimatedVisibility(
		visible = !isActionDone,
		exit = fadeOut(tween(ANIMATION_DURATION_MS))
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(bgColor, RoundedCornerShape(8.dp))
		) {
			val threshold = rowWidthPx * 0.30f
			val progress = if (rowWidthPx <= 0f) 0f else min(1f, abs(offsetX.value) / threshold)

			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(16.dp)
					.graphicsLayer { alpha = if (rowWidthPx > 0f) progress else 0f },
				contentAlignment = Alignment.CenterEnd
			) {
				val iconScale = 0.6f + (0.4f * progress)
				Icon(
					modifier = Modifier.graphicsLayer {
						scaleX = iconScale
						scaleY = iconScale
					},
					imageVector = icon,
					contentDescription = null,
					tint = iconTint,
				)
			}

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.graphicsLayer { translationX = offsetX.value }
					.pointerInput(swipeBehavior) {
						rowWidthPx = size.width.toFloat()
						detectHorizontalDragGestures(
							onDragStart = { hasCrossedThreshold = false },
							onDragEnd = {
								val crossed = abs(offsetX.value) >= rowWidthPx * 0.30f
								scope.launch {
									if (crossed) {
										offsetX.animateTo(
											targetValue = -rowWidthPx,
											animationSpec = tween(ANIMATION_DURATION_MS)
										)
										isActionDone = true
									} else {
										offsetX.animateTo(
											targetValue = 0f,
											animationSpec = spring(
												dampingRatio = Spring.DampingRatioMediumBouncy,
												stiffness = Spring.StiffnessMedium
											)
										)
									}
								}
							},
							onDragCancel = {
								scope.launch {
									offsetX.animateTo(
										0f,
										spring(
											dampingRatio = Spring.DampingRatioMediumBouncy,
											stiffness = Spring.StiffnessMedium
										)
									)
								}
							},
							onHorizontalDrag = { change, dragAmount ->
								change.consume()
								val proposed = (offsetX.value + dragAmount).coerceAtMost(0f)
								scope.launch { offsetX.snapTo(proposed) }
								val crossedNow = abs(proposed) >= rowWidthPx * 0.30f
								if (crossedNow && !hasCrossedThreshold) {
									haptic.performHapticFeedback(HapticFeedbackType.LongPress)
									hasCrossedThreshold = true
								} else if (!crossedNow && hasCrossedThreshold) {
									hasCrossedThreshold = false
								}
							},
						)
					}
			) {
				content(item)
			}
		}
	}

}
