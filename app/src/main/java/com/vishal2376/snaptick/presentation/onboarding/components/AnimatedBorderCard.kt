package com.vishal2376.snaptick.presentation.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Rounded card with a breathing primary border when [selected] (same pulse
 * cadence as the onboarding notification card). Idle state keeps a faint
 * static outline so the cards still read as containers.
 */
@Composable
fun AnimatedBorderCard(
	selected: Boolean,
	onClick: () -> Unit,
	cornerRadius: Dp = 16.dp,
	borderColor: Color = Color.Unspecified,
	idleBorderColor: Color = Color.Unspecified,
	background: Color = Color.Unspecified,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	val shape = RoundedCornerShape(cornerRadius)
	val animatedBorder by animateColorAsState(
		targetValue = if (selected) borderColor else idleBorderColor,
		animationSpec = tween(durationMillis = 240),
		label = "border-color"
	)

	val breathTransition = rememberInfiniteTransition(label = "card-breath")
	val breathAlpha by breathTransition.animateFloat(
		initialValue = 0.35f,
		targetValue = 1f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse
		),
		label = "card-breath-alpha"
	)
	val effectiveBorder = if (selected) animatedBorder.copy(alpha = breathAlpha)
		else animatedBorder

	Box(
		modifier = modifier
			.clip(shape)
			.background(background, shape)
			.border(
				width = if (selected) 2.5.dp else 1.dp,
				color = effectiveBorder,
				shape = shape
			)
			.clickable(
				interactionSource = interactionSource,
				indication = null
			) { onClick() }
	) {
		content()
	}
}
