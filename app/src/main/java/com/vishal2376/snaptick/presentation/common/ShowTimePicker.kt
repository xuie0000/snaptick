package com.vishal2376.snaptick.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import java.time.LocalTime

/**
 * Wraps WheelPickerCompose with a `key(isTimeUpdated)` rebuild trigger so
 * the wheel re-mounts only on external programmatic time changes (e.g.,
 * end time auto-computed after a start time scroll). User-driven snaps
 * update the parent state via [onSelect] but don't toggle the key, so the
 * wheel stays mounted during normal scrolling.
 *
 * The library does not expose a programmatic scroll-to; re-mount is the
 * cleanest available technique. Previous implementation used two
 * AnimatedVisibility blocks alternating, which caused a brief double-render.
 */
@Composable
fun ShowTimePicker(
	time: LocalTime,
	isTimeUpdated: Boolean = false,
	is24hourFormat: Boolean = false,
	onSelect: (LocalTime) -> Unit,
) {
	key(isTimeUpdated) {
		WheelTimePicker(
			timeFormat = if (is24hourFormat) TimeFormat.HOUR_24 else TimeFormat.AM_PM,
			startTime = time,
			textColor = MaterialTheme.colorScheme.onBackground,
			onSnappedTime = onSelect,
		)
	}
}