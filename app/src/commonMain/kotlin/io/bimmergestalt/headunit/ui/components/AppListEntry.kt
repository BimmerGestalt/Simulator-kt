package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.headunit.ui.theme.Theme
import io.bimmergestalt.headunit.utils.tintFilter

@Composable
fun AppListEntry(icon: ImageTintable?, name: String, onClick: () -> Unit) {
	Row(verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.clickable { onClick() }
			.fillMaxWidth()
			.padding(vertical = Theme.metrics.list_row_padding)
	) {
		val iconSizeModifier = Modifier
			.padding(4.dp)
			.size(32.dp)
		if (icon != null) {
			Image(icon.image, null, modifier = iconSizeModifier,
				colorFilter = if (icon.tintable) tintFilter(Theme.colorScheme.onBackground, !isSystemInDarkTheme()) else null)
		} else {
			Box(modifier = iconSizeModifier)
		}
		Text(name, style = Theme.typography.headlineSmall, color= Theme.colorScheme.onBackground)

	}
}