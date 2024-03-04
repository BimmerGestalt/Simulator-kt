package io.bimmergestalt.headunit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun HeadunitktTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	colorTheme: ColorTheme = ColorTheme.Lagoon,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		darkTheme -> colorTheme.dark
		else -> colorTheme.light
	}
	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}