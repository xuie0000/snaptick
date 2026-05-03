package com.vishal2376.snaptick.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.GitHubRelease

@Composable
fun UpdateAvailableDialog(
	release: GitHubRelease,
	onOpenInBrowser: () -> Unit,
	onDismiss: () -> Unit,
) {
	Dialog(onDismissRequest = onDismiss) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = stringResource(R.string.update_available),
				style = h1TextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)
			Text(
				text = if (release.name.isNotBlank()) release.name else release.tagName,
				style = h2TextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			if (release.body.isNotBlank()) {
				Spacer(Modifier.height(8.dp))
				Text(
					modifier = Modifier
						.heightIn(max = 220.dp)
						.verticalScroll(rememberScrollState()),
					text = release.body,
					style = taskDescTextStyle,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
			Spacer(Modifier.height(8.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.End,
				verticalAlignment = Alignment.CenterVertically
			) {
				TextButton(onClick = onDismiss) {
					Text(text = stringResource(R.string.later))
				}
				Spacer(Modifier.height(0.dp))
				Button(
					onClick = onOpenInBrowser,
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = MaterialTheme.colorScheme.onPrimary
					),
					shape = RoundedCornerShape(12.dp)
				) {
					Text(text = stringResource(R.string.open_in_browser))
				}
			}
		}
	}
}

