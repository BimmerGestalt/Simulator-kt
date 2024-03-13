package io.bimmergestalt.headunit.models

import androidx.compose.runtime.mutableStateOf
import io.bimmergestalt.headunit.ui.theme.ColorTheme

object ThemeSettings {
	val darkMode = mutableStateOf(true)
	val colorTheme = mutableStateOf(ColorTheme.Lagoon)

	var supportsDynamicColor = false
	val availableThemes
		get() = listOfNotNull(
			if (supportsDynamicColor) ColorTheme.Dynamic else null,
			ColorTheme.Pink,
			ColorTheme.Lagoon,
			ColorTheme.Leaves,
		)
}