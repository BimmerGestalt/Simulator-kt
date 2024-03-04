package io.bimmergestalt.headunit.models

import androidx.compose.runtime.mutableStateOf
import io.bimmergestalt.headunit.ui.theme.ColorTheme

object ThemeSettings {
	val darkMode = mutableStateOf(true)
	val colorTheme = mutableStateOf(ColorTheme.Lagoon)
	val availableThemes = listOf(
//		ColorTheme.Dynamic,
		ColorTheme.Pink,
		ColorTheme.Lagoon,
		ColorTheme.Leaves,
	)
}