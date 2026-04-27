package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.ShowTimePicker
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.settings.common.ToggleOptions
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import java.time.LocalTime

@Composable
fun TimePickerOptionComponent(
	isWheelTimePicker: Boolean,
	is24HourTimeFormat: Boolean,
	onSelect: (Boolean) -> Unit,
	onSelectTimeFormat: (Boolean) -> Unit
) {
	var selectedOption by remember { mutableStateOf(isWheelTimePicker) }
	var is24HourTimeFormatEnabled by remember { mutableStateOf(is24HourTimeFormat) }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {

		Text(
			text = stringResource(R.string.choose_time_picker_style),
			style = h2TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
		)
		Spacer(modifier = Modifier.height(8.dp))
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceAround
		) {
			ShowTimePicker(
				time = LocalTime.now(),
				is24hourFormat = is24HourTimeFormatEnabled,
				onSelect = {})
		}

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(8.dp),
			horizontalArrangement = Arrangement.SpaceAround
		) {
			ToggleOptions(title = stringResource(R.string.scroll), isSelected = selectedOption) {
				selectedOption = true
				onSelect(true)
			}
			ToggleOptions(title = stringResource(R.string.clock), isSelected = !selectedOption) {
				selectedOption = false
				onSelect(false)
			}
		}

		Divider(
			modifier = Modifier.padding(top = 8.dp),
			thickness = 1.dp,
			color = MaterialTheme.colorScheme.primary
		)

		Row(
			Modifier
				.fillMaxWidth()
				.padding(16.dp, 0.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.enable_24_hour_format),
				style = h3TextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)

			Switch(checked = is24HourTimeFormatEnabled,
				colors = SwitchDefaults.colors(
					checkedThumbColor = MaterialTheme.colorScheme.primary,
					checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
					checkedBorderColor = MaterialTheme.colorScheme.primary
				),
				onCheckedChange = {
					is24HourTimeFormatEnabled = !is24HourTimeFormatEnabled
					onSelectTimeFormat(is24HourTimeFormatEnabled)
				})
		}
	}

}

@Preview
@Composable
fun TimePickerOptionComponentPreview() {
	SnaptickTheme {
		TimePickerOptionComponent(false, true, onSelect = {}, {})
	}
}