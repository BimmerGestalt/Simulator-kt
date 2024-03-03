package io.bimmergestalt.headunit.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.bimmergestalt.headunit.ui.theme.ColorTheme

class ThemeSettings: ViewModel() {
	val darkMode = mutableStateOf<Boolean?>(null)
	val colorTheme = mutableStateOf(ColorTheme.Lagoon)
	val availableThemes = listOf(
		ColorTheme.Dynamic,
		ColorTheme.Pink,
		ColorTheme.Lagoon,
		ColorTheme.Leaves,
	)
}