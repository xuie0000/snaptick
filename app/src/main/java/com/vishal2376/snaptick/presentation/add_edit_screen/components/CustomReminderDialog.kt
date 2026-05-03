package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vishal2376.snaptick.presentation.common.durationTextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.wheelpicker.WheelTimePicker
import java.time.LocalTime

// Returns a "minutes before task starts" offset; seeded at 00:05 for one-tap default.
@Composable
fun CustomReminderDialog(
	onClose: () -> Unit,
	onSelect: (Int) -> Unit,
) {
	Dialog(onDismissRequest = onClose) {
		var picked = LocalTime.of(0, 5)

		Card(
			modifier = Modifier
				.fillMaxWidth()
				.border(
					4.dp,
					MaterialTheme.colorScheme.primaryContainer,
					RoundedCornerShape(16.dp)
				),
			shape = RoundedCornerShape(16.dp),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(32.dp, 16.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(24.dp)
			) {
				Text(
					text = "Custom reminder",
					color = MaterialTheme.colorScheme.onBackground,
					style = durationTextStyle
				)
				WheelTimePicker(
					startTime = LocalTime.of(0, 5),
					textColor = MaterialTheme.colorScheme.onBackground,
					onSnappedTime = { picked = it }
				)
				Text(
					modifier = Modifier
						.padding(8.dp)
						.clickable {
							val offsetMinutes = picked.hour * 60 + picked.minute
							onSelect(offsetMinutes)
							onClose()
						}
						.align(Alignment.End),
					text = "Done",
					style = h3TextStyle,
					color = MaterialTheme.colorScheme.primary
				)
			}
		}
	}
}

@Preview
@Composable
private fun CustomReminderDialogPreview() {
	SnaptickTheme {
		CustomReminderDialog(onClose = {}, onSelect = {})
	}
}
