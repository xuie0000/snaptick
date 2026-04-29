package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vishal2376.snaptick.presentation.common.h1TextStyle

@Composable
fun CustomReminderDialog(
	onClose: () -> Unit,
	onSelect: (Int) -> Unit,
) {
	var text by remember { mutableStateOf("") }
	val parsed = text.toIntOrNull()
	val valid = parsed != null && parsed in 1..1440

	Dialog(onDismissRequest = onClose) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				text = "Custom reminder",
				style = h1TextStyle,
				color = MaterialTheme.colorScheme.onBackground,
			)
			Text(
				text = "Minutes before task starts (1 to 1440)",
				color = MaterialTheme.colorScheme.onPrimaryContainer,
			)
			OutlinedTextField(
				value = text,
				onValueChange = { v -> text = v.filter(Char::isDigit).take(4) },
				singleLine = true,
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.End,
			) {
				TextButton(onClick = onClose) { Text("Cancel") }
				Button(
					onClick = {
						parsed?.let { onSelect(it) }
						onClose()
					},
					enabled = valid,
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = MaterialTheme.colorScheme.onPrimary,
					),
					shape = RoundedCornerShape(12.dp),
				) {
					Text("Add")
				}
			}
		}
	}
}
