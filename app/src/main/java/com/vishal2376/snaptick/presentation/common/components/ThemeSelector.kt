package com.vishal2376.snaptick.presentation.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.onboarding.components.AnimatedBorderCard

/**
 * Row of theme cards with the same look used by onboarding. Lives in
 * `common/components` so settings and onboarding render identical chips.
 */
@Composable
fun ThemeSelector(
	selected: AppTheme,
	onSelect: (AppTheme) -> Unit,
	modifier: Modifier = Modifier,
) {
	val haptic = LocalHapticFeedback.current
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {
		AppTheme.entries.forEach { theme ->
			ThemeCard(
				theme = theme,
				selected = theme == selected,
				onClick = {
					haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
					onSelect(theme)
				},
				modifier = Modifier.weight(1f)
			)
		}
	}
}

@Composable
private fun ThemeCard(
	theme: AppTheme,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val palette = themePalette(theme)
	val labelColor =
		if (selected) MaterialTheme.colorScheme.primary
		else MaterialTheme.colorScheme.onPrimaryContainer

	val interaction = remember { MutableInteractionSource() }
	val pressed by interaction.collectIsPressedAsState()

	val targetScale = when {
		pressed -> 0.94f
		selected -> 1.04f
		else -> 1f
	}
	val animatedScale by animateFloatAsState(
		targetValue = targetScale,
		animationSpec = spring(
			dampingRatio = Spring.DampingRatioMediumBouncy,
			stiffness = Spring.StiffnessMedium
		),
		label = "theme-scale"
	)
	val animatedLift by animateFloatAsState(
		targetValue = if (selected) -4f else 0f,
		animationSpec = spring(
			dampingRatio = 0.85f,
			stiffness = Spring.StiffnessMedium
		),
		label = "theme-lift"
	)

	val baseBg = MaterialTheme.colorScheme.primaryContainer
	val tintedBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f).compositeOver(baseBg)
	val animatedBg by animateColorAsState(
		targetValue = if (selected) tintedBg else baseBg,
		animationSpec = tween(durationMillis = 240),
		label = "theme-bg"
	)
	val density = LocalDensity.current

	AnimatedBorderCard(
		selected = selected,
		onClick = onClick,
		borderColor = MaterialTheme.colorScheme.primary,
		idleBorderColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
		background = animatedBg,
		interactionSource = interaction,
		modifier = modifier.graphicsLayer {
			scaleX = animatedScale
			scaleY = animatedScale
			translationY = with(density) { animatedLift.dp.toPx() }
		}
	) {
		Box {
			Column(
				modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				ThemeMiniPreview(palette = palette)
				Text(
					text = theme.name,
					style = if (selected) h3TextStyle else taskTextStyle,
					color = labelColor
				)
			}

			AnimatedVisibility(
				visible = selected,
				enter = fadeIn(
					animationSpec = tween(
						durationMillis = 220,
						easing = FastOutSlowInEasing
					)
				) + scaleIn(
					initialScale = 0.6f,
					animationSpec = spring(
						dampingRatio = Spring.DampingRatioMediumBouncy,
						stiffness = Spring.StiffnessMedium
					)
				),
				exit = fadeOut(),
				modifier = Modifier
					.align(Alignment.TopEnd)
					.padding(6.dp)
			) {
				Box(
					modifier = Modifier
						.size(20.dp)
						.background(MaterialTheme.colorScheme.primary, CircleShape),
					contentAlignment = Alignment.Center
				) {
					Icon(
						imageVector = Icons.Rounded.Check,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.onPrimary,
						modifier = Modifier.size(14.dp)
					)
				}
			}
		}
	}
}

@Composable
private fun ThemeMiniPreview(palette: ThemePalette) {
	val outerShape = RoundedCornerShape(8.dp)
	val cardShape = RoundedCornerShape(4.dp)
	val barShape = RoundedCornerShape(3.dp)
	val lineShape = RoundedCornerShape(2.dp)

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(64.dp)
			.background(palette.background, outerShape)
			.border(
				width = 1.dp,
				color = palette.onBackground.copy(alpha = 0.08f),
				shape = outerShape
			)
			.padding(6.dp)
	) {
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(5.dp)
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth(0.55f)
					.height(5.dp)
					.background(palette.primary, barShape)
			)
			MiniTaskRow(
				accent = palette.primary,
				lineColor = palette.onBackground.copy(alpha = 0.55f),
				container = palette.primaryContainer,
				cardShape = cardShape,
				lineShape = lineShape
			)
			MiniTaskRow(
				accent = palette.primary.copy(alpha = 0.6f),
				lineColor = palette.onBackground.copy(alpha = 0.45f),
				container = palette.primaryContainer,
				cardShape = cardShape,
				lineShape = lineShape
			)
		}
	}
}

@Composable
private fun MiniTaskRow(
	accent: Color,
	lineColor: Color,
	container: Color,
	cardShape: Shape,
	lineShape: Shape,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(container, cardShape)
			.padding(horizontal = 5.dp, vertical = 4.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp)
	) {
		Box(
			modifier = Modifier
				.size(6.dp)
				.background(accent, CircleShape)
		)
		Box(
			modifier = Modifier
				.weight(1f)
				.height(4.dp)
				.background(lineColor, lineShape)
		)
	}
}

private data class ThemePalette(
	val background: Color,
	val onBackground: Color,
	val primary: Color,
	val primaryContainer: Color,
)

private fun themePalette(theme: AppTheme): ThemePalette = when (theme) {
	AppTheme.Light -> ThemePalette(
		background = Color(0xFFEAF3FF),
		onBackground = Color(0xFF000000),
		primary = Color(0xFF83BCFF),
		primaryContainer = Color(0xFFD9E9FF),
	)

	AppTheme.Dark -> ThemePalette(
		background = Color(0xFF161A30),
		onBackground = Color(0xFFEAF3FF),
		primary = Color(0xFF83BCFF),
		primaryContainer = Color(0xFF31304D),
	)

	AppTheme.Amoled -> ThemePalette(
		background = Color(0xFF000000),
		onBackground = Color(0xFFEAF3FF),
		primary = Color(0xFF83BCFF),
		primaryContainer = Color(0xFF252526),
	)
}
