package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay

private const val AUTO_DISMISS_DELAY_MS = 5000L

// Settings-card-style inline update status with auto-dismiss for transient states.
@Composable
fun UpdateStatusComponent(
	checking: Boolean,
	failed: Boolean,
	updateAvailable: GitHubRelease?,
	lastCheckedAt: Long,
	onOpenUpdate: () -> Unit,
	onRetry: () -> Unit,
	onDismiss: () -> Unit,
) {
	val visible = checking || failed || updateAvailable != null || lastCheckedAt > 0

	// Re-key on the timestamp so a new check resets the countdown.
	val autoHideKey = if (!checking && updateAvailable == null && (failed || lastCheckedAt > 0)) {
		lastCheckedAt to failed
	} else null
	if (autoHideKey != null) {
		LaunchedEffect(autoHideKey) {
			delay(AUTO_DISMISS_DELAY_MS)
			onDismiss()
		}
	}

	AnimatedVisibility(
		visible = visible,
		enter = expandVertically() + fadeIn(),
		exit = shrinkVertically() + fadeOut(),
	) {
		when {
			checking -> StatusCard(
				accent = MaterialTheme.colorScheme.primary,
				title = stringResource(R.string.checking_for_updates),
				leading = {
					CircularProgressIndicator(
						modifier = Modifier.size(18.dp),
						strokeWidth = 2.dp,
						color = MaterialTheme.colorScheme.primary,
					)
				},
				onDismiss = null,
			)

			updateAvailable != null -> StatusCard(
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
				ctaLabel = stringResource(R.string.update_check_open),
				onCta = onOpenUpdate,
				onDismiss = onDismiss,
			)

			failed -> StatusCard(
				accent = Red,
				title = stringResource(R.string.update_check_failed),
				leading = {
					StatusIcon(icon = Icons.Filled.Error, tint = Red)
				},
				ctaLabel = stringResource(R.string.update_check_retry),
				onCta = onRetry,
				onDismiss = onDismiss,
			)

			else -> StatusCard(
				accent = LightGreen,
				title = stringResource(R.string.up_to_date),
				leading = {
					StatusIcon(icon = Icons.Filled.CheckCircle, tint = LightGreen)
				},
				onDismiss = onDismiss,
			)
		}
	}
}

@Composable
private fun StatusCard(
	accent: Color,
	title: String,
	leading: @Composable () -> Unit,
	ctaLabel: String? = null,
	onCta: (() -> Unit)? = null,
	onDismiss: (() -> Unit)?,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 24.dp, vertical = 4.dp)
			.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp))
			.padding(horizontal = 14.dp, vertical = 12.dp),
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
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
		if (ctaLabel != null && onCta != null) {
			Spacer(modifier = Modifier.width(4.dp))
			Text(
				modifier = Modifier
					.background(accent, CircleShape)
					.clickable { onCta() }
					.padding(horizontal = 14.dp, vertical = 6.dp),
				text = ctaLabel,
				style = h3TextStyle,
				color = MaterialTheme.colorScheme.onPrimary,
			)
		}
		if (onDismiss != null) {
			Icon(
				imageVector = Icons.Default.Close,
				contentDescription = "Dismiss",
				tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
				modifier = Modifier
					.size(20.dp)
					.clickable { onDismiss() }
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
