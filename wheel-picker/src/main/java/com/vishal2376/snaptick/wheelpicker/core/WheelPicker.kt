package com.vishal2376.snaptick.wheelpicker.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.chrisbanes.snapper.SnapperLayoutInfo
import dev.chrisbanes.snapper.rememberLazyListSnapperLayoutInfo
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlin.math.absoluteValue

// Virtual repetitions for wrap mode. Enough that hitting an edge is impractical.
private const val WRAP_REPEAT = 1000

@Composable
internal fun WheelPicker(
	modifier: Modifier = Modifier,
	startIndex: Int = 0,
	scrollToIndex: Int? = null,
	scrollGeneration: Int = 0,
	count: Int,
	rowCount: Int,
	wrapAround: Boolean = false,
	size: DpSize = DpSize(128.dp, 128.dp),
	selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
	onScrollFinished: (snappedIndex: Int) -> Int? = { null },
	content: @Composable LazyItemScope.(index: Int) -> Unit,
) {
	val virtualCount = if (wrapAround) count * WRAP_REPEAT else count
	// Land mid-range so swipes either direction can't hit an edge.
	val initialVirtualIndex = if (wrapAround) {
		(WRAP_REPEAT / 2) * count + startIndex.coerceIn(0, count - 1)
	} else startIndex

	val lazyListState = rememberLazyListState(initialVirtualIndex)
	val snapperLayoutInfo = rememberLazyListSnapperLayoutInfo(lazyListState = lazyListState)
	val isScrollInProgress = lazyListState.isScrollInProgress
	var suppressNextSnapCallback by remember { mutableStateOf(false) }

	LaunchedEffect(scrollGeneration) {
		if (scrollGeneration > 0 && scrollToIndex != null) {
			val target = scrollToIndex.coerceIn(0, count - 1)
			val currentVirtual = lazyListState.firstVisibleItemIndex
			val virtualTarget = if (wrapAround) {
				// Closest virtual occurrence so the wheel doesn't spin a full cycle.
				val currentReal = ((currentVirtual % count) + count) % count
				val forwardDelta = ((target - currentReal) % count + count) % count
				val backwardDelta = forwardDelta - count
				val delta = if (forwardDelta <= -backwardDelta) forwardDelta else backwardDelta
				currentVirtual + delta
			} else target
			if (virtualTarget != currentVirtual) {
				suppressNextSnapCallback = true
				lazyListState.animateScrollToItem(virtualTarget)
			}
		}
	}

	LaunchedEffect(isScrollInProgress, count) {
		if (!isScrollInProgress) {
			if (suppressNextSnapCallback) {
				suppressNextSnapCallback = false
				return@LaunchedEffect
			}
			val snappedVirtual = calculateSnappedItemIndex(snapperLayoutInfo) ?: initialVirtualIndex
			val snappedReal = if (wrapAround) ((snappedVirtual % count) + count) % count
				else snappedVirtual
			onScrollFinished(snappedReal)?.let { reqReal ->
				// Stay on the current virtual page; otherwise we'd teleport across the wrap.
				val virtualLand = if (wrapAround) {
					val page = snappedVirtual / count
					page * count + reqReal.coerceIn(0, count - 1)
				} else reqReal
				lazyListState.scrollToItem(virtualLand)
			}
		}
	}

	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		if (selectorProperties.enabled().value) {
			Surface(
				modifier = Modifier
					.size(size.width, size.height / rowCount),
				shape = selectorProperties.shape().value,
				color = selectorProperties.color().value,
				border = selectorProperties.border().value
			) {}
		}
		LazyColumn(
			modifier = Modifier
				.height(size.height)
				.width(size.width),
			state = lazyListState,
			contentPadding = PaddingValues(vertical = size.height / rowCount * ((rowCount - 1) / 2)),
			flingBehavior = rememberSnapperFlingBehavior(
				lazyListState = lazyListState
			)
		) {
			items(virtualCount) { virtualIndex ->
				val realIndex = if (wrapAround) virtualIndex % count else virtualIndex
				val rotationX = calculateAnimatedRotationX(
					lazyListState = lazyListState,
					snapperLayoutInfo = snapperLayoutInfo,
					index = virtualIndex,
					rowCount = rowCount
				)
				Box(
					modifier = Modifier
						.height(size.height / rowCount)
						.width(size.width)
						.alpha(
							calculateAnimatedAlpha(
								lazyListState = lazyListState,
								snapperLayoutInfo = snapperLayoutInfo,
								index = virtualIndex,
								rowCount = rowCount
							)
						)
						.graphicsLayer {
							this.rotationX = rotationX
						},
					contentAlignment = Alignment.Center
				) {
					content(realIndex)
				}
			}
		}
	}
}

private fun calculateSnappedItemIndex(snapperLayoutInfo: SnapperLayoutInfo): Int? {
	var currentItemIndex = snapperLayoutInfo.currentItem?.index

	if (snapperLayoutInfo.currentItem?.offset != 0) {
		if (currentItemIndex != null) {
			currentItemIndex++
		}
	}
	return currentItemIndex
}

@Composable
private fun calculateAnimatedAlpha(
	lazyListState: LazyListState,
	snapperLayoutInfo: SnapperLayoutInfo,
	index: Int,
	rowCount: Int
): Float {
	val distanceToIndexSnap = snapperLayoutInfo.distanceToIndexSnap(index).absoluteValue
	val layoutInfo = remember { derivedStateOf { lazyListState.layoutInfo } }.value
	val viewPortHeight = layoutInfo.viewportSize.height.toFloat()
	val singleViewPortHeight = viewPortHeight / rowCount

	return if (distanceToIndexSnap in 0..singleViewPortHeight.toInt()) {
		1.2f - (distanceToIndexSnap / singleViewPortHeight)
	} else {
		0.2f
	}
}

@Composable
private fun calculateAnimatedRotationX(
	lazyListState: LazyListState,
	snapperLayoutInfo: SnapperLayoutInfo,
	index: Int,
	rowCount: Int
): Float {
	val distanceToIndexSnap = snapperLayoutInfo.distanceToIndexSnap(index)
	val layoutInfo = remember { derivedStateOf { lazyListState.layoutInfo } }.value
	val viewPortHeight = layoutInfo.viewportSize.height.toFloat()
	val singleViewPortHeight = viewPortHeight / rowCount
	val animatedRotationX = -20f * (distanceToIndexSnap / singleViewPortHeight)

	return if (animatedRotationX.isNaN()) {
		0f
	} else {
		animatedRotationX
	}
}

object WheelPickerDefaults {
	@Composable
	fun selectorProperties(
		enabled: Boolean = true,
		shape: Shape = RoundedCornerShape(16.dp),
		color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
		border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
	): SelectorProperties = DefaultSelectorProperties(
		enabled = enabled,
		shape = shape,
		color = color,
		border = border
	)
}

interface SelectorProperties {
	@Composable
	fun enabled(): State<Boolean>

	@Composable
	fun shape(): State<Shape>

	@Composable
	fun color(): State<Color>

	@Composable
	fun border(): State<BorderStroke?>
}

@Immutable
internal class DefaultSelectorProperties(
	private val enabled: Boolean,
	private val shape: Shape,
	private val color: Color,
	private val border: BorderStroke?
) : SelectorProperties {

	@Composable
	override fun enabled(): State<Boolean> = rememberUpdatedState(enabled)

	@Composable
	override fun shape(): State<Shape> = rememberUpdatedState(shape)

	@Composable
	override fun color(): State<Color> = rememberUpdatedState(color)

	@Composable
	override fun border(): State<BorderStroke?> = rememberUpdatedState(border)
}
