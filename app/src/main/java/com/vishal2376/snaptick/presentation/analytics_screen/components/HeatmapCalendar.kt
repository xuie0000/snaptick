package com.vishal2376.snaptick.presentation.analytics_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.domain.model.DailyStats
import java.time.LocalDate

/**
 * 7-row weekly heatmap for the supplied daily map. Cell intensity tracks
 * completion ratio. Empty cells (no tasks scheduled) render dim. Renders
 * column-major: each column is one week, top row is Monday.
 */
@Composable
fun HeatmapCalendar(
	daily: Map<LocalDate, DailyStats>,
	modifier: Modifier = Modifier,
) {
	if (daily.isEmpty()) return
	val sorted = daily.keys.sorted()
	val end = sorted.last()
	val start = sorted.first()
	val totalDays = (end.toEpochDay() - start.toEpochDay() + 1).toInt()
	val weeks = (totalDays + 6) / 7
	val cellSize = 14.dp
	val cellGap = 3.dp
	val baseColor = MaterialTheme.colorScheme.primary

	Column(
		modifier = modifier
			.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
			.padding(12.dp),
		verticalArrangement = Arrangement.spacedBy(cellGap),
	) {
		for (row in 0..6) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(cellGap),
			) {
				for (col in 0 until weeks) {
					val dayIndex = col * 7 + row
					if (dayIndex >= totalDays) {
						Box(modifier = Modifier.size(cellSize))
						continue
					}
					val date = start.plusDays(dayIndex.toLong())
					val stats = daily[date]
					val ratio = stats?.ratio ?: 0f
					val alpha = if (stats == null || stats.total == 0) 0.10f
					else 0.20f + 0.80f * ratio.coerceIn(0f, 1f)
					Box(
						modifier = Modifier
							.size(cellSize)
							.background(
								baseColor.copy(alpha = alpha),
								RoundedCornerShape(3.dp),
							),
					)
				}
			}
		}
	}
}
