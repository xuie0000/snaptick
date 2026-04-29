package com.vishal2376.snaptick.presentation.onboarding.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.data.calendar.CalendarInfo
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.Yellow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Check

@Composable
fun RestoreAndSyncPage(
	calendarSyncEnabled: Boolean,
	notificationsEnabled: Boolean,
	writableCalendars: List<CalendarInfo>,
	selectedCalendarId: Long?,
	onRestoreClick: () -> Unit,
	onPickIcsClick: () -> Unit,
	onCalendarSyncToggle: (Boolean) -> Unit,
	onSelectCalendar: (Long) -> Unit,
	onEnableNotifications: () -> Unit,
) {
	val haptic = LocalHapticFeedback.current
	var showCalendarDialog by remember { mutableStateOf(false) }

	if (showCalendarDialog) {
		CalendarPickerDialog(
			writableCalendars = writableCalendars,
			selectedCalendarId = selectedCalendarId,
			onSelect = { id ->
				haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
				onSelectCalendar(id)
				if (!calendarSyncEnabled) onCalendarSyncToggle(true)
				showCalendarDialog = false
			},
			onDismiss = {
				// User exited without picking - keep sync off so we don't end
				// up enabled with no target calendar.
				showCalendarDialog = false
				if (calendarSyncEnabled) onCalendarSyncToggle(false)
			},
		)
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 24.dp, vertical = 16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Restore Progress",
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(6.dp))
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp),
			text = "Bring your tasks across or sync to your device calendar.",
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(24.dp))

		ActionCard(
			iconRes = R.drawable.ic_import,
			accent = Blue,
			title = "Restore from backup",
			subtitle = "Pick a Snaptick .json backup to load every task and setting.",
			onClick = {
				haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
				onRestoreClick()
			}
		)
		Spacer(Modifier.height(12.dp))

		ActionCard(
			iconRes = R.drawable.ic_calendar_sync,
			accent = Yellow,
			title = "Import .ics file",
			subtitle = "One-tap import from any calendar export. Adds events as tasks.",
			onClick = {
				haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
				onPickIcsClick()
			}
		)
		Spacer(Modifier.height(12.dp))

		val onCalendarToggle: (Boolean) -> Unit = { enabled ->
			haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
			if (enabled) {
				// Don't flip the toggle on yet; only flip it on after the user
				// picks a calendar. If they cancel, the toggle stays off.
				showCalendarDialog = true
			} else {
				onCalendarSyncToggle(false)
			}
		}

		ActionCard(
			iconRes = R.drawable.ic_calendar_sync,
			accent = LightGreen,
			title = "Sync to device calendar",
			subtitle = "Mirror every task to your device calendar automatically.",
			trailing = {
				Switch(
					checked = calendarSyncEnabled,
					onCheckedChange = onCalendarToggle,
					colors = SwitchDefaults.colors(
						checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
						checkedTrackColor = MaterialTheme.colorScheme.primary
					)
				)
			},
			onClick = { onCalendarToggle(!calendarSyncEnabled) }
		)

		AnimatedVisibility(visible = !notificationsEnabled) {
			Column {
				Spacer(Modifier.height(20.dp))
				LabeledDivider(label = "Permission required")
				Spacer(Modifier.height(14.dp))
				NotificationActionCard(
					notificationsEnabled = notificationsEnabled,
					onEnable = {
						haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
						onEnableNotifications()
					}
				)
			}
		}

		Spacer(Modifier.height(16.dp))
		Text(
			text = "You can change these anytime from Settings.",
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
			textAlign = TextAlign.Center,
			modifier = Modifier.fillMaxWidth()
		)
	}
}

@Composable
private fun ActionCard(
	iconRes: Int,
	accent: Color,
	title: String,
	subtitle: String,
	trailing: @Composable () -> Unit = { ChevronTrailing() },
	onClick: () -> Unit,
) {
	val interaction = remember { MutableInteractionSource() }
	val pressed by interaction.collectIsPressedAsState()
	val scale by animateFloatAsState(
		targetValue = if (pressed) 0.96f else 1f,
		animationSpec = spring(
			dampingRatio = Spring.DampingRatioMediumBouncy,
			stiffness = Spring.StiffnessMedium
		),
		label = "press-scale"
	)

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.graphicsLayer { scaleX = scale; scaleY = scale }
			.background(
				MaterialTheme.colorScheme.primaryContainer,
				RoundedCornerShape(18.dp)
			)
			.clickable(
				interactionSource = interaction,
				indication = null,
				onClick = onClick
			)
			.padding(horizontal = 16.dp, vertical = 16.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(14.dp)
	) {
		Box(
			modifier = Modifier
				.size(48.dp)
				.background(accent.copy(alpha = 0.22f), RoundedCornerShape(14.dp)),
			contentAlignment = Alignment.Center
		) {
			Icon(
				painter = painterResource(iconRes),
				contentDescription = null,
				tint = accent,
				modifier = Modifier.size(24.dp)
			)
		}
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = title,
				style = h3TextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			Spacer(Modifier.height(4.dp))
			Text(
				text = subtitle,
				style = infoDescTextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
			)
		}
		trailing()
	}
}

@Composable
private fun LabeledDivider(label: String) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		Divider(
			modifier = Modifier.weight(1f),
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.18f),
			thickness = 1.dp
		)
		Text(
			text = label,
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
			modifier = Modifier.padding(horizontal = 12.dp)
		)
		Divider(
			modifier = Modifier.weight(1f),
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.18f),
			thickness = 1.dp
		)
	}
}

@Composable
private fun NotificationActionCard(
	notificationsEnabled: Boolean,
	onEnable: () -> Unit,
) {
	val breathTransition = rememberInfiniteTransition(label = "notif-breath")
	val breathAlpha by breathTransition.animateFloat(
		initialValue = 0.25f,
		targetValue = 1f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse
		),
		label = "notif-breath-alpha"
	)

	if (notificationsEnabled) {
		ActionCard(
			iconRes = R.drawable.ic_clock,
			accent = Red,
			title = "Enable notifications",
			subtitle = "Required for reminders and Pomodoro timer to work properly.",
			trailing = {
				Switch(
					checked = notificationsEnabled,
					onCheckedChange = { if (it) onEnable() },
					colors = SwitchDefaults.colors(
						checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
						checkedTrackColor = MaterialTheme.colorScheme.primary
					)
				)
			},
			onClick = { if (!notificationsEnabled) onEnable() }
		)
		return
	}

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.border(
				width = 2.dp,
				color = Red.copy(alpha = breathAlpha),
				shape = RoundedCornerShape(20.dp)
			)
	) {
		ActionCard(
			iconRes = R.drawable.ic_clock,
			accent = Red,
			title = "Enable notifications",
			subtitle = "Required for reminders and Pomodoro timer to work properly.",
			trailing = {
				Switch(
					checked = notificationsEnabled,
					onCheckedChange = { if (it) onEnable() },
					colors = SwitchDefaults.colors(
						checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
						checkedTrackColor = MaterialTheme.colorScheme.primary
					)
				)
			},
			onClick = { if (!notificationsEnabled) onEnable() }
		)
	}
}

@Composable
private fun CalendarPickerDialog(
	writableCalendars: List<CalendarInfo>,
	selectedCalendarId: Long?,
	onSelect: (Long) -> Unit,
	onDismiss: () -> Unit,
) {
	Dialog(onDismissRequest = onDismiss) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp))
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				text = "Pick a calendar",
				style = h1TextStyle,
				color = MaterialTheme.colorScheme.onBackground,
			)
			if (writableCalendars.isEmpty()) {
				Text(
					text = "No writable calendars found. Enable later from Settings.",
					style = infoDescTextStyle,
					color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
				)
			} else {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.heightIn(max = 320.dp)
						.verticalScroll(rememberScrollState()),
					verticalArrangement = Arrangement.spacedBy(6.dp)
				) {
					writableCalendars.forEach { cal ->
						OnboardingCalendarRow(
							info = cal,
							selected = cal.id == selectedCalendarId,
							onClick = { onSelect(cal.id) }
						)
					}
				}
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.End,
			) {
				TextButton(onClick = onDismiss) {
					Text(text = "Cancel")
				}
			}
		}
	}
}

@Composable
private fun OnboardingCalendarRow(
	info: CalendarInfo,
	selected: Boolean,
	onClick: () -> Unit,
) {
	val shape = RoundedCornerShape(10.dp)
	val rowMod = if (selected) {
		Modifier
			.fillMaxWidth()
			.clickable { onClick() }
			.background(MaterialTheme.colorScheme.primaryContainer, shape)
			.border(2.dp, MaterialTheme.colorScheme.primary, shape)
			.padding(horizontal = 10.dp, vertical = 12.dp)
	} else {
		Modifier
			.fillMaxWidth()
			.clickable { onClick() }
			.background(Color.Transparent, shape)
			.padding(horizontal = 10.dp, vertical = 12.dp)
	}
	Row(
		modifier = rowMod,
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
			if (info.accountName.isNotBlank()) {
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
				tint = MaterialTheme.colorScheme.primary
			)
		}
	}
}

@Composable
private fun ChevronTrailing() {
	Icon(
		imageVector = Icons.Rounded.ChevronRight,
		contentDescription = null,
		tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
	)
}
