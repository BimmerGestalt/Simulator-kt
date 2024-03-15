package io.bimmergestalt.headunit.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Metrics(
	val list_row_padding: Dp
) {
}

val MaterialMetrics = Metrics(
	list_row_padding = 4.dp
)

val BavariaMetrics = Metrics(
	list_row_padding = 2.dp
)