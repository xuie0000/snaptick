package com.vishal2376.snaptick.presentation.pomodoro_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.taskDescTextStyle

@Composable
fun ReplacePomodoroDialog(
	runningTaskTitle: String,
	onConfirm: () -> Unit,
	onDismiss: () -> Unit,
) {
	Dialog(onDismissRequest = onDismiss) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				text = "Stop running pomodoro?",
				style = h1TextStyle,
				color = MaterialTheme.colorScheme.onBackground,
			)
			Text(
				text = "Only one pomodoro can run at a time. Stop the timer for \"" +
					runningTaskTitle + "\" and start the new one?",
				style = taskDescTextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer,
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.End,
			) {
				TextButton(onClick = onDismiss) { Text("Keep current") }
				Button(
					onClick = onConfirm,
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = MaterialTheme.colorScheme.onPrimary,
					),
					shape = RoundedCornerShape(12.dp),
				) {
					Text("Stop and start new")
				}
			}
		}
	}
}
