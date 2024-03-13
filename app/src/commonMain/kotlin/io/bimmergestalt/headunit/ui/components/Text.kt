package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.bimmergestalt.headunit.ui.theme.Theme

@Composable
fun SectionHeader(value: String, color: Color = Theme.colorScheme.onBackground) {
	Text(value, modifier = Modifier.padding(14.dp),
		color=color, style = Theme.typography.titleMedium)
}