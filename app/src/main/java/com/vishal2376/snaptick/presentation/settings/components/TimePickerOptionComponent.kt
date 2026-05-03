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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

/**
 * Card-style picker mirroring the theme picker UX. Two preview cards
 * (wheel vs clock) sit side-by-side; the 24-hour-format toggle sits below.
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
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = stringResource(R.string.choose_time_picker_style),
			style = h2TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp),
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

		Divider(
			modifier = Modifier.padding(top = 4.dp),
			thickness = 1.dp,
			color = MaterialTheme.colorScheme.primary
		)

		Row(
			Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.enable_24_hour_format),
				style = h3TextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)
			Switch(
				checked = is24HourTimeFormat,
				colors = SwitchDefaults.colors(
					checkedThumbColor = MaterialTheme.colorScheme.primary,
					checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
					checkedBorderColor = MaterialTheme.colorScheme.primary
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
	val baseBg = MaterialTheme.colorScheme.primaryContainer
	val tintedBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f).compositeOver(baseBg)
	val animatedBg by animateColorAsState(
		targetValue = if (selected) tintedBg else baseBg,
		animationSpec = tween(durationMillis = 220),
		label = "picker-bg"
	)
	val animatedScale by animateFloatAsState(
		targetValue = if (selected) 1.04f else 1f,
		animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
		label = "picker-scale"
	)
	val borderColor = if (selected) MaterialTheme.colorScheme.primary
		else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.18f)
	val borderWidth = if (selected) 2.dp else 1.dp
	val labelStyle = if (selected) h3TextStyle else taskTextStyle
	val labelColor = if (selected) MaterialTheme.colorScheme.primary
		else MaterialTheme.colorScheme.onPrimaryContainer

	Column(
		modifier = modifier
			.graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
			.background(animatedBg, RoundedCornerShape(14.dp))
			.border(borderWidth, borderColor, RoundedCornerShape(14.dp))
			.clickable { onClick() }
			.padding(vertical = 14.dp, horizontal = 10.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(10.dp)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(72.dp),
			contentAlignment = Alignment.Center
		) { preview() }
		Text(text = label, style = labelStyle, color = labelColor)
	}
}

@Composable
private fun WheelMiniPreview() {
	val rowShape = RoundedCornerShape(6.dp)
	val highlight = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
	val ink = MaterialTheme.colorScheme.onPrimaryContainer
	Column(
		modifier = Modifier
			.fillMaxWidth(0.9f)
			.background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
			.padding(horizontal = 10.dp, vertical = 6.dp),
		verticalArrangement = Arrangement.spacedBy(2.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text("06", style = taskTextStyle, color = ink.copy(alpha = 0.35f))
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(20.dp)
				.background(highlight, rowShape),
			contentAlignment = Alignment.Center
		) { Text("07 : 30", style = h3TextStyle, color = ink) }
		Text("08", style = taskTextStyle, color = ink.copy(alpha = 0.35f))
	}
}

@Composable
private fun ClockMiniPreview(is24Hour: Boolean) {
	val ink = MaterialTheme.colorScheme.onPrimaryContainer
	Box(
		modifier = Modifier
			.size(64.dp)
			.background(MaterialTheme.colorScheme.background, CircleShape)
			.border(2.dp, ink.copy(alpha = 0.4f), CircleShape),
		contentAlignment = Alignment.Center
	) {
		// Hour hand
		Box(
			modifier = Modifier
				.size(width = 2.dp, height = 16.dp)
				.rotate(45f)
				.background(ink, RoundedCornerShape(1.dp))
		)
		// Minute hand
		Box(
			modifier = Modifier
				.size(width = 2.dp, height = 22.dp)
				.rotate(180f)
				.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp))
		)
		Box(
			modifier = Modifier
				.size(4.dp)
				.background(ink, CircleShape)
		)
		// 24h badge
		if (is24Hour) {
			Text(
				text = "24",
				style = taskTextStyle,
				color = MaterialTheme.colorScheme.primary,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(bottom = 4.dp)
			)
		}
	}
}

@Preview
@Composable
private fun TimePickerOptionComponentPreview() {
	SnaptickTheme {
		TimePickerOptionComponent(false, true, onSelect = {}, {})
	}
}
