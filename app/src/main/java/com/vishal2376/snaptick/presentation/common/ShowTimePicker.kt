package com.vishal2376.snaptick.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.vishal2376.snaptick.wheelpicker.WheelTimePicker
import com.vishal2376.snaptick.wheelpicker.core.TimeFormat
import java.time.LocalTime

// Vendored wheel reacts to external startTime changes without remount or feedback loop.
@Composable
fun ShowTimePicker(
	time: LocalTime,
	is24hourFormat: Boolean = false,
	onSelect: (LocalTime) -> Unit,
) {
	WheelTimePicker(
		timeFormat = if (is24hourFormat) TimeFormat.HOUR_24 else TimeFormat.AM_PM,
		startTime = time,
		textColor = MaterialTheme.colorScheme.onBackground,
		onSnappedTime = onSelect,
	)
}
