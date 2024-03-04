package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun AppListEntry(icon: ImageBitmap?, name: String, onClick: () -> Unit) {
	Row(verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.clickable { onClick() }
			.fillMaxWidth()
			.padding(vertical = 4.dp)
	) {
		val iconSizeModifier = Modifier
			.padding(4.dp)
			.size(32.dp)
		if (icon != null) {
			Image(icon, null, modifier = iconSizeModifier)
		} else {
			Box(modifier = iconSizeModifier)
		}
		Text(name, style = MaterialTheme.typography.headlineSmall, color= MaterialTheme.colorScheme.primary)

	}
}