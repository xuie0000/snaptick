package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.components.ThemeSelector
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

/**
 * Renders the same theme cards used by onboarding so the two surfaces stay
 * in sync. Material You toggle stays underneath, applies on top of the
 * picked base theme.
 */
@Composable
fun ThemeOptionComponent(
	defaultTheme: AppTheme,
	dynamicTheme: Boolean,
	onSelect: (AppTheme) -> Unit,
	onChangedDynamicTheme: (Boolean) -> Unit
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		ThemeSelector(
			selected = defaultTheme,
			onSelect = onSelect,
			modifier = Modifier.padding(horizontal = 24.dp)
		)
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp, vertical = 4.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				imageVector = Icons.Default.Palette,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onBackground
			)
			Text(
				modifier = Modifier
					.weight(1f)
					.padding(start = 8.dp),
				text = stringResource(R.string.material_you),
				style = h2TextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)

			Switch(
				checked = dynamicTheme,
				onCheckedChange = onChangedDynamicTheme,
				colors = SwitchDefaults.colors(
					checkedThumbColor = MaterialTheme.colorScheme.primary,
					checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
					uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer,
					checkedBorderColor = MaterialTheme.colorScheme.primary
				)
			)
		}
	}
}

@Preview
@Composable
fun ThemeOptionComponentPreview() {
	SnaptickTheme {
		ThemeOptionComponent(
			defaultTheme = AppTheme.Light,
			dynamicTheme = true,
			onChangedDynamicTheme = {},
			onSelect = {})
	}
}
