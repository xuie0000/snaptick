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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

/**
 * Time-picker style chooser. 60-30-10 read:
 *  - 60% background (the sheet itself + card surface)
 *  - 30% primaryContainer (selected card tint, format toggle row)
 *  - 10% primary (selected ring, accent strokes, hands)
 */
@Composable
fun TimePickerOptionComponent(
	isWheelTimePicker: Boolean,
	is24HourTimeFormat: Boolean,
	onSelect: (Boolean) -> Unit,
	onSelectTimeFormat: (Boolean) -> Unit
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text(
			text = stringResource(R.string.choose_time_picker_style),
			style = h2TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			PickerStyleCard(
				label = stringResource(R.string.scroll),
				selected = isWheelTimePicker,
				onClick = { onSelect(true) },
				modifier = Modifier.weight(1f)
			) { WheelMiniPreview() }
			PickerStyleCard(
				label = stringResource(R.string.clock),
				selected = !isWheelTimePicker,
				onClick = { onSelect(false) },
				modifier = Modifier.weight(1f)
			) { ClockMiniPreview(is24Hour = is24HourTimeFormat) }
		}

		// 24-hour format row in primaryContainer pill (30% layer).
		Row(
			Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.background(
					MaterialTheme.colorScheme.primaryContainer,
					RoundedCornerShape(12.dp)
				)
				.padding(horizontal = 16.dp, vertical = 8.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.enable_24_hour_format),
				style = h3TextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			Switch(
				checked = is24HourTimeFormat,
				colors = SwitchDefaults.colors(
					checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
					checkedTrackColor = MaterialTheme.colorScheme.primary,
					uncheckedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
					uncheckedTrackColor = MaterialTheme.colorScheme.background,
				),
				onCheckedChange = onSelectTimeFormat,
			)
		}
	}
}

@Composable
private fun PickerStyleCard(
	label: String,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	preview: @Composable () -> Unit,
) {
	// Card sits on background (60%); selection lifts to primaryContainer (30%)
	// with a primary outline (10%).
	val baseBg = MaterialTheme.colorScheme.background
	val activeBg = MaterialTheme.colorScheme.primaryContainer
	val animatedBg by animateColorAsState(
		targetValue = if (selected) activeBg else baseBg,
		animationSpec = tween(durationMillis = 200),
		label = "picker-bg"
	)
	val animatedScale by animateFloatAsState(
		targetValue = if (selected) 1.03f else 1f,
		animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
		label = "picker-scale"
	)
	val borderColor = if (selected) MaterialTheme.colorScheme.primary
		else MaterialTheme.colorScheme.primaryContainer
	val borderWidth = if (selected) 2.dp else 1.dp
	val labelColor = if (selected) MaterialTheme.colorScheme.primary
		else MaterialTheme.colorScheme.onBackground

	Column(
		modifier = modifier
			.graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
			.background(animatedBg, RoundedCornerShape(16.dp))
			.border(borderWidth, borderColor, RoundedCornerShape(16.dp))
			.clickable { onClick() }
			.padding(vertical = 16.dp, horizontal = 12.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(80.dp),
			contentAlignment = Alignment.Center
		) { preview() }
		Text(
			text = label,
			style = if (selected) h3TextStyle else taskTextStyle,
			color = labelColor,
			fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
		)
	}
}

@Composable
private fun WheelMiniPreview() {
	val ink = MaterialTheme.colorScheme.onBackground
	val container = MaterialTheme.colorScheme.primaryContainer
	val accent = MaterialTheme.colorScheme.primary
	val highlightShape = RoundedCornerShape(8.dp)

	Row(
		modifier = Modifier
			.background(container, RoundedCornerShape(10.dp))
			.padding(horizontal = 6.dp, vertical = 4.dp),
		horizontalArrangement = Arrangement.spacedBy(2.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		WheelColumn(values = listOf("06", "07", "08"), selectedIndex = 1, ink = ink, accent = accent, highlightShape = highlightShape)
		Text(":", style = h3TextStyle, color = ink, modifier = Modifier.padding(horizontal = 2.dp))
		WheelColumn(values = listOf("29", "30", "31"), selectedIndex = 1, ink = ink, accent = accent, highlightShape = highlightShape)
	}
}

@Composable
private fun WheelColumn(
	values: List<String>,
	selectedIndex: Int,
	ink: Color,
	accent: Color,
	highlightShape: androidx.compose.ui.graphics.Shape,
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(2.dp)
	) {
		values.forEachIndexed { index, value ->
			val isSelected = index == selectedIndex
			val color = if (isSelected) ink else ink.copy(alpha = 0.35f)
			val style = if (isSelected) h3TextStyle else taskTextStyle
			val mod = if (isSelected) {
				Modifier
					.background(accent.copy(alpha = 0.18f), highlightShape)
					.border(1.dp, accent, highlightShape)
					.padding(horizontal = 8.dp, vertical = 2.dp)
			} else Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
			Text(text = value, style = style, color = color, modifier = mod)
		}
	}
}

@Composable
private fun ClockMiniPreview(is24Hour: Boolean) {
	val face = MaterialTheme.colorScheme.background
	val ringColor = MaterialTheme.colorScheme.primaryContainer
	val ink = MaterialTheme.colorScheme.onBackground
	val accent = MaterialTheme.colorScheme.primary

	Box(
		modifier = Modifier
			.size(72.dp)
			.background(face, CircleShape)
			.border(3.dp, ringColor, CircleShape),
		contentAlignment = Alignment.Center
	) {
		// Tick marks at 12 / 3 / 6 / 9.
		listOf(0f, 90f, 180f, 270f).forEach { angle ->
			Box(
				modifier = Modifier
					.size(width = 2.dp, height = 6.dp)
					.graphicsLayer {
						rotationZ = angle
						translationY = -28.dp.toPx()
					}
					.background(ink.copy(alpha = 0.55f), RoundedCornerShape(1.dp))
			)
		}
		// Hour hand (short, ink color, ~10:00 angle = -60deg).
		Box(
			modifier = Modifier
				.size(width = 3.dp, height = 18.dp)
				.graphicsLayer {
					rotationZ = -60f
					translationY = -8.dp.toPx()
				}
				.background(ink, RoundedCornerShape(1.5.dp))
		)
		// Minute hand (long, accent color, 12:10 = 60deg).
		Box(
			modifier = Modifier
				.size(width = 2.dp, height = 26.dp)
				.graphicsLayer {
					rotationZ = 60f
					translationY = -12.dp.toPx()
				}
				.background(accent, RoundedCornerShape(1.dp))
		)
		// Center pivot.
		Box(
			modifier = Modifier
				.size(6.dp)
				.background(accent, CircleShape)
		)
		if (is24Hour) {
			Text(
				text = "24",
				style = taskTextStyle,
				color = accent,
				fontWeight = FontWeight.Bold,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(bottom = 6.dp)
			)
		}
	}
}

@Preview
@Composable
private fun TimePickerOptionComponentPreview() {
	SnaptickTheme {
		TimePickerOptionComponent(true, true, onSelect = {}, {})
	}
}
