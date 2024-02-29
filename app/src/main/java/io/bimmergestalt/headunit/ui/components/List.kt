package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.bmw.idrive.BMWRemoting
import de.bmw.idrive.BMWRemoting.RHMIResourceIdentifier
import io.bimmergestalt.headunit.utils.asBoolean
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIComponent
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIProperty
import kotlin.math.max
import kotlin.math.min

fun parseColumnWidths(widths: Any?): List<Dp?> {
	return (widths as? String)?.split(",")?.map { it.trim().toIntOrNull()?.dp } ?: emptyList()
}

@Composable
fun List(component: RHMIComponent.List, modifier: Modifier = Modifier,
         eventHandler: (componentId: Int, eventId: Int, args: Map<*, *>) -> Unit, onClickAction: (RHMIAction?, Map<Int, Any>?) -> Unit) {
	val columnWidths = parseColumnWidths(component.properties[RHMIProperty.PropertyId.LIST_COLUMNWIDTH.id]?.value)
	val model = component.getModel()?.value
	if (model != null) {
		val listState = rememberLazyListState()
		if (component.properties[RHMIProperty.PropertyId.VALID.id]?.value?.asBoolean() == false) {
			val requestedWindow by remember { derivedStateOf {
				val firstMod = listState.firstVisibleItemIndex / 10
					max(0, firstMod - 10) to
							min(model.endIndex, firstMod + 30)
				}
			}
			val preparedWindow = (requestedWindow.first until requestedWindow.second).map {
				model[it]
			}
			if (preparedWindow.any { it.isEmpty() }) {
				LaunchedEffect(requestedWindow) {
					eventHandler(component.id, 2, mapOf(5 to requestedWindow.first, 6 to requestedWindow.second))
				}
			}
		}

		Table(
			columnCount = model.width,
			rowCount = model.height,
			verticalLazyListState = listState,
			rowModifier = modifier.heightIn(48.dp, 96.dp),
			onClickRow = { rowIndex -> onClickAction(component.getAction(), mapOf(1 to rowIndex)) }
		) { col, row ->
			val data = model[row].getOrNull(col)
			val maybeWidth = columnWidths.getOrNull(col)
			var cellModifier: Modifier = Modifier.padding(6.dp)
			if (maybeWidth != null) {
				cellModifier = cellModifier.width(maybeWidth)
			}
			val richText = component.getModel()?.modelType == "richText"
			Cell(data, modifier = cellModifier, richText = richText)
		}
	}
}

@Composable
fun Cell(data: Any?, modifier: Modifier = Modifier, richText: Boolean = false) {
	val isByteArray = data is ByteArray
	val isImageData = data is BMWRemoting.RHMIResourceData && data.type == BMWRemoting.RHMIResourceType.IMAGEDATA
	val isImageId = data is RHMIResourceIdentifier && data.type == BMWRemoting.RHMIResourceType.IMAGEID
	if (isByteArray || isImageData || isImageId) {
		ImageCell(data, modifier.heightIn(48.dp, 96.dp))
	} else {
		val maxLines = if (richText) Int.MAX_VALUE else 2
		Text(data?.toString() ?: "", maxLines = maxLines, softWrap = richText,
			modifier = modifier, color = MaterialTheme.colorScheme.primary)
	}
}

@Composable
fun SimpleList(items: List<Any>) {
	Table(
		columnCount = 1,
		rowCount = items.size
	) { _, row ->
		Text(items[row].toString(), maxLines=2)
	}
}

@Preview()
@Composable
fun ListPreview() {
	SimpleList(items = listOf(
		"App1",
		"App2",
		"App3"
	))
}