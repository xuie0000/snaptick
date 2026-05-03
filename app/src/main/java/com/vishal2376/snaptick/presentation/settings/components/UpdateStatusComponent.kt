package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.GitHubRelease
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Red

/**
 * Inline status the user sees right under the Updates row. Replaces the
 * earlier toast-only feedback so the result of a check sticks long enough
 * to read and act on.
 */
@Composable
fun UpdateStatusComponent(
	checking: Boolean,
	failed: Boolean,
	updateAvailable: GitHubRelease?,
	lastCheckedAt: Long,
	onOpenUpdate: () -> Unit,
	onRetry: () -> Unit,
) {
	val visible = checking || failed || updateAvailable != null || lastCheckedAt > 0
	AnimatedVisibility(visible = visible) {
		when {
			checking -> StatusRow(
				accent = MaterialTheme.colorScheme.primary,
				title = stringResource(R.string.checking_for_updates),
				leading = {
					CircularProgressIndicator(
						modifier = Modifier.size(18.dp),
						strokeWidth = 2.dp,
						color = MaterialTheme.colorScheme.primary,
					)
				}
			)
			updateAvailable != null -> StatusRow(
				accent = MaterialTheme.colorScheme.primary,
				title = stringResource(
					R.string.update_available_with_version,
					updateAvailable.tagName.removePrefix("v")
				),
				leading = {
					StatusIcon(
						icon = Icons.Filled.NewReleases,
						tint = MaterialTheme.colorScheme.primary
					)
				},
				trailingAction = stringResource(R.string.update_check_open),
				onTrailingClick = onOpenUpdate,
			)
			failed -> StatusRow(
				accent = Red,
				title = stringResource(R.string.update_check_failed),
				leading = {
					StatusIcon(icon = Icons.Filled.Error, tint = Red)
				},
				trailingAction = stringResource(R.string.update_check_retry),
				onTrailingClick = onRetry,
			)
			else -> StatusRow(
				accent = LightGreen,
				title = stringResource(R.string.up_to_date),
				leading = {
					StatusIcon(icon = Icons.Filled.CheckCircle, tint = LightGreen)
				}
			)
		}
	}
}

@Composable
private fun StatusRow(
	accent: Color,
	title: String,
	leading: @Composable () -> Unit,
	trailingAction: String? = null,
	onTrailingClick: (() -> Unit)? = null,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 24.dp, vertical = 4.dp)
			.background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
			.border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
			.padding(horizontal = 12.dp, vertical = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(10.dp)
	) {
		Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
			leading()
		}
		Text(
			modifier = Modifier.weight(1f),
			text = title,
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)
		if (trailingAction != null && onTrailingClick != null) {
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				modifier = Modifier
					.background(accent.copy(alpha = 0.15f), CircleShape)
					.clickable { onTrailingClick() }
					.padding(horizontal = 12.dp, vertical = 6.dp),
				text = trailingAction,
				style = h3TextStyle,
				color = accent,
			)
		}
	}
}

@Composable
private fun StatusIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
	Icon(
		imageVector = icon,
		contentDescription = null,
		tint = tint,
		modifier = Modifier.size(18.dp)
	)
}
