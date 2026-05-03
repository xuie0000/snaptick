package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.data.calendar.CalendarInfo
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle

@Composable
fun CalendarSyncOptionComponent(
	enabled: Boolean,
	selectedCalendarId: Long?,
	writableCalendars: List<CalendarInfo>,
	onEnabledChange: (Boolean) -> Unit,
	onCalendarSelected: (Long) -> Unit,
	onSyncAllNow: () -> Unit,
) {
	val selectedCalendar = writableCalendars.firstOrNull { it.id == selectedCalendarId }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 4.dp, vertical = 8.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		SheetTitle(text = stringResource(R.string.calendar_sync))

		// Enable card: icon + title + switch.
		ToggleCard(
			icon = Icons.Filled.CalendarMonth,
			title = stringResource(R.string.sync_tasks_to_device_calendar),
			checked = enabled,
			onCheckedChange = onEnabledChange,
		)

		AnimatedVisibility(
			visible = enabled,
			enter = expandVertically() + fadeIn(),
			exit = shrinkVertically() + fadeOut(),
		) {
			Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
				SectionLabel(stringResource(R.string.pick_calendar))
				if (writableCalendars.isEmpty()) {
					EmptyCalendarHint()
				} else {
					Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
						writableCalendars.forEach { cal ->
							CalendarRow(
								info = cal,
								selected = cal.id == selectedCalendarId,
								onClick = { onCalendarSelected(cal.id) }
							)
						}
					}
				}
				Spacer(Modifier.height(4.dp))
				Button(
					onClick = onSyncAllNow,
					enabled = selectedCalendar != null,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp),
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = MaterialTheme.colorScheme.onPrimary,
						disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
					),
					shape = RoundedCornerShape(12.dp)
				) {
					Icon(
						imageVector = Icons.Filled.Sync,
						contentDescription = null,
						modifier = Modifier.size(18.dp)
					)
					Spacer(Modifier.size(8.dp))
					Text(
						text = stringResource(R.string.sync_all_tasks_now),
						style = h3TextStyle,
					)
				}
			}
		}
	}
}

@Composable
private fun ToggleCard(
	icon: androidx.compose.ui.graphics.vector.ImageVector,
	title: String,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
			.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp))
			.padding(horizontal = 14.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Box(
			modifier = Modifier
				.size(38.dp)
				.background(
					MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
					RoundedCornerShape(10.dp)
				),
			contentAlignment = Alignment.Center
		) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
				modifier = Modifier.size(20.dp)
			)
		}
		Text(
			modifier = Modifier.weight(1f),
			text = title,
			style = h3TextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
		Switch(
			checked = checked,
			onCheckedChange = onCheckedChange,
			colors = SwitchDefaults.colors(
				checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
				checkedTrackColor = MaterialTheme.colorScheme.primary,
				uncheckedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
				uncheckedTrackColor = MaterialTheme.colorScheme.background,
			)
		)
	}
}

@Composable
private fun SectionLabel(text: String) {
	Text(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 20.dp),
		text = text,
		style = infoDescTextStyle,
		color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
	)
}

@Composable
private fun EmptyCalendarHint() {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
			.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
			.padding(horizontal = 14.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = stringResource(R.string.no_writable_calendars_found),
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
	}
}

@Composable
private fun CalendarRow(
	info: CalendarInfo,
	selected: Boolean,
	onClick: () -> Unit,
) {
	val shape = RoundedCornerShape(12.dp)
	val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
		else MaterialTheme.colorScheme.primaryContainer
	val border = if (selected) 2.dp else 0.dp
	val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
			.clickable { onClick() }
			.background(bg, shape)
			.then(if (border > 0.dp) Modifier.border(border, borderColor, shape) else Modifier)
			.padding(horizontal = 12.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Box(
			modifier = Modifier
				.size(14.dp)
				.background(Color(0xFF000000.toInt() or info.colorArgb), CircleShape)
		)
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = info.displayName.ifBlank { info.accountName },
				style = taskTextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)
			if (info.accountName.isNotBlank() && info.accountName != info.displayName) {
				Text(
					text = info.accountName,
					style = taskDescTextStyle,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}
		if (selected) {
			Icon(
				imageVector = Icons.Default.Check,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
				modifier = Modifier.size(20.dp)
			)
		}
	}
}
