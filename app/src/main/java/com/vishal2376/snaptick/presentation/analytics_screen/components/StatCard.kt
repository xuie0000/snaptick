package com.vishal2376.snaptick.presentation.analytics_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.taskDescTextStyle

@Composable
fun StatCard(
	title: String,
	value: String,
	accent: Color,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
			.padding(horizontal = 14.dp, vertical = 12.dp),
	) {
		Text(
			text = title,
			style = taskDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
		)
		Text(
			text = value,
			style = h1TextStyle,
			color = accent,
		)
	}
}
