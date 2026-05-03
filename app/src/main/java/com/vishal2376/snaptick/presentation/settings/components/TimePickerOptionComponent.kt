package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

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
		verticalArrangement = Arrangement.spacedBy(14.dp)
	) {
		SheetTitle(text = stringResource(R.string.choose_time_picker_style))

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp))
				.padding(4.dp),
			horizontalArrangement = Arrangement.spacedBy(4.dp),
		) {
			SegmentOption(
				label = stringResource(R.string.scroll),
				icon = Icons.Outlined.SwapVert,
				selected = isWheelTimePicker,
				onClick = { onSelect(true) },
				modifier = Modifier.weight(1f),
			)
			SegmentOption(
				label = stringResource(R.string.clock),
				icon = Icons.Outlined.AccessTime,
				selected = !isWheelTimePicker,
				onClick = { onSelect(false) },
				modifier = Modifier.weight(1f),
			)
		}

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp))
				.padding(horizontal = 16.dp, vertical = 6.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
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
private fun SegmentOption(
	label: String,
	icon: ImageVector,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val bg by animateColorAsState(
		targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
		animationSpec = tween(200),
		label = "segment-bg"
	)
	val fg by animateColorAsState(
		targetValue = if (selected) MaterialTheme.colorScheme.onPrimary
		else MaterialTheme.colorScheme.onPrimaryContainer,
		animationSpec = tween(200),
		label = "segment-fg"
	)

	Box(
		modifier = modifier
			.background(bg, RoundedCornerShape(10.dp))
			.clickable { onClick() }
			.padding(vertical = 12.dp),
		contentAlignment = Alignment.Center,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				tint = fg,
				modifier = Modifier.size(18.dp)
			)
			Text(
				text = label,
				style = if (selected) h3TextStyle else taskTextStyle,
				color = fg,
				fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
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
