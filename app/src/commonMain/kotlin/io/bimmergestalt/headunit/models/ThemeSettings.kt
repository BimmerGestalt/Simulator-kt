package io.bimmergestalt.headunit.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.bimmergestalt.headunit.ui.theme.Appearance
import io.bimmergestalt.headunit.ui.theme.ColorTheme

object ThemeSettings {
	var darkMode by mutableStateOf(true)
	var colorTheme by mutableStateOf(ColorTheme.Lagoon)

	var supportsDynamicColor = false
	val availableThemes
		get() = listOfNotNull(
			if (supportsDynamicColor) ColorTheme.Dynamic else null,
			ColorTheme.Pink,
			ColorTheme.Lagoon,
			ColorTheme.Leaves,
		)

	var appearance by mutableStateOf(Appearance.Material)
	val availableAppearances = listOf(
		Appearance.Material,
		Appearance.Bavaria,
	)
}