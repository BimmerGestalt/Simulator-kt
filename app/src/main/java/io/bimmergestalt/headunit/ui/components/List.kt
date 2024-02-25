package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIComponent
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIProperty

fun parseColumnWidths(widths: Any?): List<Dp?> {
	return (widths as? String)?.split(",")?.map { it.trim().toIntOrNull()?.dp } ?: emptyList()
}

@Composable
fun List(component: RHMIComponent.List) {
	val columnWidths = parseColumnWidths(component.properties[RHMIProperty.PropertyId.LIST_COLUMNWIDTH.id]?.value)
	val model = component.getModel()?.value
	if (model != null) {
		Table(
			columnCount = model.width,
			rowCount = model.height,

		) { col, row ->
			val data = model[row][col].toString()
			val maybeWidth = columnWidths.getOrNull(col)
			var modifier: Modifier = Modifier
			if (maybeWidth != null) {
				modifier = modifier.width(maybeWidth)
			}
			val maxLines = if (component.getModel()?.modelType == "richText") Int.MAX_VALUE else 2
			Text(data, modifier = modifier, maxLines = maxLines)
		}
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