package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.SwipeBehavior
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

/**
 * Card-style swipe-action picker. Each card shows a mini task row with the
 * action background already revealed on the right edge so the user sees
 * exactly what will happen on a left swipe.
 */
@Composable
fun SwipeActionOptionComponent(
	selected: SwipeBehavior,
	onSelect: (SwipeBehavior) -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		SheetTitle(text = stringResource(R.string.choose_swipe_action))

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			horizontalArrangement = Arrangement.spacedBy(10.dp),
		) {
			SwipeActionCard(
				label = stringResource(R.string.none),
				icon = Icons.Default.Block,
				accent = MaterialTheme.colorScheme.primary,
				selected = selected == SwipeBehavior.NONE,
				onClick = { onSelect(SwipeBehavior.NONE) },
				modifier = Modifier.weight(1f)
			)
			SwipeActionCard(
				label = stringResource(R.string.delete),
				icon = Icons.Default.Delete,
				accent = Red,
				selected = selected == SwipeBehavior.DELETE,
				onClick = { onSelect(SwipeBehavior.DELETE) },
				modifier = Modifier.weight(1f)
			)
			SwipeActionCard(
				label = stringResource(R.string.complete),
				icon = Icons.Default.Check,
				accent = LightGreen,
				selected = selected == SwipeBehavior.COMPLETE,
				onClick = { onSelect(SwipeBehavior.COMPLETE) },
				modifier = Modifier.weight(1f)
			)
		}
	}
}

@Composable
private fun SwipeActionCard(
	label: String,
	icon: ImageVector,
	accent: Color,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val baseBg = MaterialTheme.colorScheme.primaryContainer
	val tintedBg = accent.copy(alpha = 0.10f).compositeOver(baseBg)
	val animatedBg by animateColorAsState(
		targetValue = if (selected) tintedBg else baseBg,
		animationSpec = tween(220),
		label = "swipe-bg"
	)
	val animatedScale by animateFloatAsState(
		targetValue = if (selected) 1.04f else 1f,
		animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
		label = "swipe-scale"
	)
	val borderColor = if (selected) accent else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.18f)
	val borderWidth = if (selected) 2.dp else 1.dp

	Column(
		modifier = modifier
			.graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
			.background(animatedBg, RoundedCornerShape(14.dp))
			.border(borderWidth, borderColor, RoundedCornerShape(14.dp))
			.clickable { onClick() }
			.padding(vertical = 12.dp, horizontal = 8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		SwipePreview(accent = accent, icon = icon)
		Text(
			text = label,
			style = if (selected) h3TextStyle else taskTextStyle,
			color = if (selected) accent else MaterialTheme.colorScheme.onPrimaryContainer
		)
	}
}

@Composable
private fun SwipePreview(accent: Color, icon: ImageVector) {
	val outerShape = RoundedCornerShape(8.dp)
	val rowShape = RoundedCornerShape(6.dp)
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(40.dp)
			.background(accent.copy(alpha = 0.85f), outerShape),
	) {
		// Action icon sits on the right edge — that's where the swipe reveals it.
		Icon(
			imageVector = icon,
			contentDescription = null,
			tint = Color.White,
			modifier = Modifier
				.align(Alignment.CenterEnd)
				.padding(end = 8.dp)
				.size(16.dp)
		)
		// Row hugs the LEFT and is partially slid left of its full width, so
		// the colored action zone is exposed on the RIGHT (right-to-left swipe).
		Box(
			modifier = Modifier
				.align(Alignment.CenterStart)
				.fillMaxWidth(0.7f)
				.height(40.dp)
				.background(MaterialTheme.colorScheme.background, rowShape)
				.padding(horizontal = 6.dp, vertical = 6.dp),
			contentAlignment = Alignment.CenterStart
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth(0.7f)
					.height(4.dp)
					.background(
						MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f),
						RoundedCornerShape(2.dp)
					)
			)
		}
	}
}

@Preview
@Composable
private fun SwipeActionOptionComponentPreview() {
	SnaptickTheme {
		SwipeActionOptionComponent(selected = SwipeBehavior.DELETE, onSelect = {})
	}
}
