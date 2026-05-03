package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.wheelpicker.WheelTimePicker
import com.vishal2376.snaptick.wheelpicker.core.TimeFormat
import java.time.LocalTime

@Composable
fun SleepTimeOptionComponent(defaultSleepTime: LocalTime, onSelect: (LocalTime) -> Unit) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		SheetTitle(text = stringResource(R.string.set_sleep_time))
		Spacer(modifier = Modifier.height(8.dp))
		WheelTimePicker(
			timeFormat = TimeFormat.AM_PM,
			startTime = defaultSleepTime,
			minTime = LocalTime.MIDNIGHT,
			maxTime = LocalTime.MAX,
			textColor = MaterialTheme.colorScheme.onBackground,
			onSnappedTime = {
				val sleepTime = LocalTime.of(it.hour, it.minute)
				onSelect(sleepTime)
			}
		)
	}
}

@Preview
@Composable
fun SleepTimeOptionComponentPreview() {
	SleepTimeOptionComponent(defaultSleepTime = LocalTime.MAX, onSelect = {})
}
