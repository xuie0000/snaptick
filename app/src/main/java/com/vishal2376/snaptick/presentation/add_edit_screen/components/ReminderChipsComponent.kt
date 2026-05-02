package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.taskTextStyle

private const val MAX_REMINDERS = 4

@Composable
fun ReminderChipsComponent(
	visible: Boolean,
	selectedOffsets: List<Int>,
	onRemoveOffset: (Int) -> Unit,
	onCustomClick: () -> Unit,
) {
	AnimatedVisibility(visible = visible) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp, vertical = 8.dp)
				.horizontalScroll(rememberScrollState()),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			selectedOffsets.forEach { offset ->
				ReminderChip(
					label = labelFor(offset),
					onDismiss = { onRemoveOffset(offset) }
				)
			}
			if (selectedOffsets.size < MAX_REMINDERS) {
				AddChip(onClick = onCustomClick)
			}
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
	onDismiss: () -> Unit,
) {
	val shape = RoundedCornerShape(20.dp)
	Row(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.primaryContainer, shape)
			.border(1.dp, MaterialTheme.colorScheme.primary, shape)
			.padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = label,
			style = taskTextStyle,
			color = MaterialTheme.colorScheme.onBackground,
		)
		Spacer(modifier = Modifier.width(6.dp))
		Icon(
			imageVector = Icons.Default.Close,
			contentDescription = "Remove $label",
			tint = MaterialTheme.colorScheme.onPrimaryContainer,
			modifier = Modifier
				.size(18.dp)
				.clickable { onDismiss() }
		)
	}
}

@Composable
private fun AddChip(onClick: () -> Unit) {
	val shape = RoundedCornerShape(20.dp)
	Row(
		modifier = Modifier
			.background(MaterialTheme.colorScheme.primaryContainer, shape)
			.border(1.dp, MaterialTheme.colorScheme.primary, shape)
			.clickable { onClick() }
			.padding(horizontal = 10.dp, vertical = 6.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Icon(
			imageVector = Icons.Default.Add,
			contentDescription = "Add reminder",
			tint = MaterialTheme.colorScheme.primary,
			modifier = Modifier.size(20.dp)
		)
	}
}
