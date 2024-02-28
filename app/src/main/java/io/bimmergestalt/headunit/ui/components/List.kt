package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.bmw.idrive.BMWRemoting
import de.bmw.idrive.BMWRemoting.RHMIResourceIdentifier
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIComponent
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIProperty

fun parseColumnWidths(widths: Any?): List<Dp?> {
	return (widths as? String)?.split(",")?.map { it.trim().toIntOrNull()?.dp } ?: emptyList()
}

@Composable
fun List(component: RHMIComponent.List, modifier: Modifier = Modifier, onClickAction: (RHMIAction?, Map<Int, Any>?) -> Unit) {
	val columnWidths = parseColumnWidths(component.properties[RHMIProperty.PropertyId.LIST_COLUMNWIDTH.id]?.value)
	val model = component.getModel()?.value
	if (model != null) {
		Table(
			columnCount = model.width,
			rowCount = model.height,
			modifier = modifier,
			onClickRow = { rowIndex -> onClickAction(component.getAction(), mapOf(1 to rowIndex)) }
		) { col, row ->
			val data = model[row][col]
			val maybeWidth = columnWidths.getOrNull(col)
			var modifier: Modifier = Modifier.padding(6.dp)
			if (maybeWidth != null) {
				modifier = modifier.width(maybeWidth)
			}
			val maxLines = if (component.getModel()?.modelType == "richText") Int.MAX_VALUE else 2
			Cell(data, modifier = modifier, maxLines = maxLines)
		}
	}
}

@Composable
fun Cell(data: Any?, modifier: Modifier = Modifier, maxLines: Int = Int.MAX_VALUE) {
	val isByteArray = data is ByteArray
	val isImageData = data is BMWRemoting.RHMIResourceData && data.type == BMWRemoting.RHMIResourceType.IMAGEDATA
	val isImageId = data is RHMIResourceIdentifier && data.type == BMWRemoting.RHMIResourceType.IMAGEID
	if (isByteArray || isImageData || isImageId) {
		ImageCell(data, modifier.heightIn(48.dp, 96.dp))
	} else {
		Text(data?.toString() ?: "", maxLines = maxLines, color = MaterialTheme.colorScheme.primary)
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