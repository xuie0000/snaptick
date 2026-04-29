package com.vishal2376.snaptick.presentation.analytics_screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.domain.model.DailyStats

/**
 * Lightweight Canvas line chart for daily completion ratio. Avoids the
 * compose-charts library footprint for this simple case; the heatmap is
 * what justifies the dependency and lives in the sibling component.
 */
@Composable
fun CompletionLineChart(
	daily: List<DailyStats>,
	modifier: Modifier = Modifier,
) {
	val lineColor = MaterialTheme.colorScheme.primary
	val gridColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.25f)
	Canvas(
		modifier = modifier
			.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
			.padding(12.dp),
	) {
		if (daily.isEmpty()) return@Canvas
		val w = size.width
		val h = size.height
		drawLine(
			color = gridColor,
			start = Offset(0f, h),
			end = Offset(w, h),
			strokeWidth = 1.5f,
		)
		drawLine(
			color = gridColor,
			start = Offset(0f, h / 2f),
			end = Offset(w, h / 2f),
			strokeWidth = 1f,
		)
		val stepX = if (daily.size > 1) w / (daily.size - 1).toFloat() else 0f
		val path = Path()
		daily.forEachIndexed { i, d ->
			val x = i * stepX
			val y = h - (d.ratio.coerceIn(0f, 1f) * h)
			if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
		}
		drawPath(
			path = path,
			color = lineColor,
			style = Stroke(width = 4f, cap = StrokeCap.Round),
		)
	}
}
