package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.taskTextStyle

private val PRESET_OFFSETS = listOf(0, 5, 10, 15, 30)

@Composable
fun ReminderChipsComponent(
	visible: Boolean,
	selectedOffsets: List<Int>,
	onToggleOffset: (Int) -> Unit,
	onCustomClick: () -> Unit,
) {
	AnimatedVisibility(visible = visible) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				PRESET_OFFSETS.forEach { offset ->
					ReminderChip(
						label = labelFor(offset),
						selected = offset in selectedOffsets,
						onClick = { onToggleOffset(offset) },
						modifier = Modifier.weight(1f)
					)
				}
			}
			val hasCustom = selectedOffsets.any { it !in PRESET_OFFSETS }
			ReminderChip(
				label = if (hasCustom) "Custom: ${selectedOffsets.firstOrNull { it !in PRESET_OFFSETS }} min before"
				else "Custom",
				selected = hasCustom,
				onClick = onCustomClick,
				modifier = Modifier.fillMaxWidth(),
			)
		}
	}
}

private fun labelFor(offsetMinutes: Int): String = when (offsetMinutes) {
	0 -> "On time"
	else -> "$offsetMinutes min"
}

@Composable
private fun ReminderChip(
	label: String,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val shape = RoundedCornerShape(8.dp)
	val mod = if (selected) {
		modifier
			.clickable { onClick() }
			.background(MaterialTheme.colorScheme.primaryContainer, shape)
			.border(2.dp, MaterialTheme.colorScheme.primary, shape)
			.padding(vertical = 10.dp, horizontal = 8.dp)
	} else {
		modifier
			.clickable { onClick() }
			.border(2.dp, MaterialTheme.colorScheme.primaryContainer, shape)
			.padding(vertical = 10.dp, horizontal = 8.dp)
	}
	Row(
		modifier = mod,
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = label,
			style = taskTextStyle,
			color = MaterialTheme.colorScheme.onBackground,
		)
	}
}
