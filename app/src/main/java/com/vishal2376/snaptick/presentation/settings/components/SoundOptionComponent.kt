package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle

@Composable
fun SoundOptionComponent(
	enabled: Boolean,
	onToggle: (Boolean) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 4.dp, vertical = 8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text(
			text = stringResource(R.string.sounds),
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			modifier = Modifier.padding(horizontal = 8.dp)
		)
		Spacer(Modifier.height(4.dp))
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				modifier = Modifier.weight(1f),
				text = stringResource(R.string.enable_sounds),
				style = h3TextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)
			Switch(
				checked = enabled,
				onCheckedChange = onToggle,
				colors = SwitchDefaults.colors(
					checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
					checkedTrackColor = MaterialTheme.colorScheme.primary
				)
			)
		}
	}
}
