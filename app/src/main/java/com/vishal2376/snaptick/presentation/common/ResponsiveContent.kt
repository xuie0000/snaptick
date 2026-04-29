package com.vishal2376.snaptick.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Caps content width on wide screens (tablets / phones in landscape) so
 * lists and forms don't stretch edge-to-edge across a 1024dp display. The
 * default 640dp cap matches a comfortable reading column. Pass [maxWidth]
 * to override per screen.
 */
@Composable
fun ResponsiveColumn(
	modifier: Modifier = Modifier,
	maxWidthDp: Int = 640,
	verticalArrangement: Arrangement.Vertical = Arrangement.Top,
	horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
	content: @Composable () -> Unit,
) {
	Column(
		modifier = modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Column(
			modifier = Modifier
				.widthIn(max = maxWidthDp.dp)
				.fillMaxSize(),
			verticalArrangement = verticalArrangement,
			horizontalAlignment = horizontalAlignment,
		) {
			content()
		}
	}
}
