package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout

/**
 * https://stackoverflow.com/a/71665355/169035
 */
@Composable
fun Table(
	modifier: Modifier = Modifier,
	rowModifier: Modifier = Modifier,
	verticalLazyListState: LazyListState = rememberLazyListState(),
	horizontalScrollState: ScrollState = rememberScrollState(),
	verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
	horizontalAlignment: Alignment.Horizontal = Alignment.Start,
	onClickRow: ((rowIndex: Int) -> Unit)? = null,
	columnCount: Int,
	rowCount: Int,
	beforeRow: (@Composable (rowIndex: Int) -> Unit)? = null,
	afterRow: (@Composable (rowIndex: Int) -> Unit)? = null,
	cellContent: @Composable (columnIndex: Int, rowIndex: Int) -> Unit
) {
	val columnWidths = remember { mutableStateMapOf<Int, Int>() }

	Box(modifier = modifier.then(Modifier.horizontalScroll(horizontalScrollState))) {
		LazyColumn(state = verticalLazyListState) {
			items(rowCount) { rowIndex ->
				Column {
					beforeRow?.invoke(rowIndex)
					val clickableRowModifier = if (onClickRow != null) {
						rowModifier.clickable { onClickRow(rowIndex) }
					} else { rowModifier }
					Row(modifier = clickableRowModifier, verticalAlignment = verticalAlignment) {
						(0 until columnCount).forEach { columnIndex ->
							Box(propagateMinConstraints = true, modifier = Modifier.layout { measurable, constraints ->
								val placeable = measurable.measure(constraints)

								val existingWidth = columnWidths[columnIndex] ?: 0
								val maxWidth = maxOf(existingWidth, placeable.width)

								if (maxWidth > existingWidth) {
									columnWidths[columnIndex] = maxWidth
								}

								layout(width = maxWidth, height = placeable.height) {
									val start = when(horizontalAlignment) {
										Alignment.Start -> 0
										Alignment.CenterHorizontally -> maxWidth/2 - placeable.width/2
										Alignment.End -> maxWidth - placeable.width
										else -> 0
									}
									placeable.placeRelative(start, 0)
								}
							}) {
								cellContent(columnIndex, rowIndex)
							}
						}
					}

					afterRow?.invoke(rowIndex)
				}
			}
		}
	}
}