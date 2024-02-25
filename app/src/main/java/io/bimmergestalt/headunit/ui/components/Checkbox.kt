package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LabelledCheckbox(
		state: Boolean,
		onCheckedChange: (Boolean) -> Unit = { },
		modifier: Modifier = Modifier.fillMaxWidth(),
		label: @Composable RowScope.() -> Unit) {
	Row(modifier = modifier.clickable { onCheckedChange(!state) }, verticalAlignment = Alignment.CenterVertically) {
		Checkbox(checked=state, onCheckedChange = onCheckedChange)
		label()
	}
}