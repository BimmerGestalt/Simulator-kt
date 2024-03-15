package io.bimmergestalt.headunit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import io.bimmergestalt.headunit.models.ThemeSettings

@Composable
fun HeadunitktTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	materialColorTheme: ColorTheme = ColorTheme.Lagoon,
	content: @Composable () -> Unit
) {
	val materialColorScheme = when {
		darkTheme -> materialColorTheme.dark
		else -> materialColorTheme.light
	}

	val themeViewModel = ThemeSettings
	val colorScheme = themeViewModel.appearance.colorScheme ?: materialColorScheme

	CompositionLocalProvider(
		LocalAppearance provides themeViewModel.appearance
	) {
		MaterialTheme(
			colorScheme = colorScheme,
			typography = when(Theme.appearance) {
				Appearance.Material -> MaterialTypography
				Appearance.Bavaria -> BavariaTypography
			},
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

	val metrics: Metrics
		@Composable
		get() = appearance.metrics
	val checkboxColors: CheckboxColors
		@Composable
		get() = when(appearance) {
			Appearance.Material -> CheckboxDefaults.colors()
			else -> CheckboxDefaults.colors(
				checkmarkColor = colorScheme.onBackground,
				checkedColor = Color.Transparent,
				uncheckedColor = Color.Transparent,
			)
		}
	val radioButtonColors: RadioButtonColors
		@Composable
		get() = when(appearance) {
			Appearance.Material -> RadioButtonDefaults.colors()
			else -> RadioButtonDefaults.colors(
				selectedColor = colorScheme.onBackground,
			)
		}
}