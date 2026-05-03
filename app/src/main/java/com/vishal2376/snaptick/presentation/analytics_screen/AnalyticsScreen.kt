package com.vishal2376.snaptick.presentation.analytics_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.AnalyticsSnapshot
import com.vishal2376.snaptick.presentation.analytics_screen.components.CompletionLineChart
import com.vishal2376.snaptick.presentation.analytics_screen.components.HeatmapCalendar
import com.vishal2376.snaptick.presentation.analytics_screen.components.StatCard
import com.vishal2376.snaptick.presentation.analytics_screen.state.AnalyticsState
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.utils.formatDurationTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
	state: AnalyticsState,
	onBack: () -> Unit,
) {
	Scaffold(topBar = {
		TopAppBar(
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = stringResource(R.string.analytics),
					style = h1TextStyle,
				)
			},
			navigationIcon = {
				IconButton(onClick = onBack) {
					Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
				}
			},
		)
	}) { innerPadding ->
		Box(modifier = Modifier
			.padding(innerPadding)
			.fillMaxSize()) {
			val snap = state.snapshot
			if (state.isLoading || snap == null) {
				Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
					CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
				}
			} else {
				AnalyticsContent(snap)
			}
		}
	}
}

@Composable
private fun AnalyticsContent(snap: AnalyticsSnapshot) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			StatCard(
				modifier = Modifier.weight(1f),
				title = "Current streak",
				value = "${snap.streak.current} d",
				accent = MaterialTheme.colorScheme.primary,
			)
			StatCard(
				modifier = Modifier.weight(1f),
				title = "Longest streak",
				value = "${snap.streak.longest} d",
				accent = MaterialTheme.colorScheme.primary,
			)
		}

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			StatCard(
				modifier = Modifier.weight(1f),
				title = "Pomodoro",
				value = "${snap.pomodoroSessionsCompleted}",
				accent = MaterialTheme.colorScheme.primary,
			)
			StatCard(
				modifier = Modifier.weight(1f),
				title = "Avg duration",
				value = formatDurationTimestamp(snap.averageTaskDurationSec),
				accent = MaterialTheme.colorScheme.primary,
			)
		}

		StatCard(
			modifier = Modifier.fillMaxWidth(),
			title = "Total time invested",
			value = formatDurationTimestamp(snap.totalTimeInvestedSec),
			accent = MaterialTheme.colorScheme.primary,
		)

		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = "Last 30 days completion",
			style = h2TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
		)
		CompletionLineChart(
			daily = snap.daily.values.toList(),
			modifier = Modifier
				.fillMaxWidth()
				.height(180.dp),
		)

		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = "Heatmap",
			style = h2TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
		)
		HeatmapCalendar(
			daily = snap.daily,
			modifier = Modifier.fillMaxWidth(),
		)
		Spacer(modifier = Modifier.height(8.dp))
	}
}

