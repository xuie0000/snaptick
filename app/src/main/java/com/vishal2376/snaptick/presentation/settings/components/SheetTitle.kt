package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.h2TextStyle

/**
 * Shared title used at the top of every settings bottom sheet. Centered,
 * h2 weight, primary text color — same look across every sheet so the
 * surface feels like one family.
 */
@Composable
fun SheetTitle(text: String, modifier: Modifier = Modifier) {
	Text(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
		text = text,
		style = h2TextStyle,
		color = MaterialTheme.colorScheme.onBackground,
		textAlign = TextAlign.Center,
	)
	Spacer(modifier = Modifier.height(8.dp))
}
