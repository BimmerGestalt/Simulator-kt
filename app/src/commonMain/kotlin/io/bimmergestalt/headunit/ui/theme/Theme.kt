package io.bimmergestalt.headunit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.bimmergestalt.headunit.models.ThemeSettings

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

	val themeViewModel = ThemeSettings
	CompositionLocalProvider(
		LocalAppearance provides themeViewModel.appearance
	) {
		MaterialTheme(
			colorScheme = colorScheme,
			typography = Typography,
			content = content
		)
	}
}

object Theme {
	// inherit from Material Theme
	val colorScheme: ColorScheme
		@Composable get() = MaterialTheme.colorScheme
	val typography: Typography
		@Composable get() = MaterialTheme.typography
	val shapes: Shapes
		@Composable get() = MaterialTheme.shapes
	val appearance: Appearance
		@Composable get() = LocalAppearance.current
}