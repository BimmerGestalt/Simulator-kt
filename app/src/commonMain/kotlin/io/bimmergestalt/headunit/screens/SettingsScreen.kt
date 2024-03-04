package io.bimmergestalt.headunit.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import io.bimmergestalt.headunit.models.ThemeSettings
import io.bimmergestalt.headunit.ui.components.LabelledCheckbox
import io.bimmergestalt.headunit.ui.components.LabelledRadioButton

object SettingsScreen: Screen {
	@Composable
	override fun Content() {
		val themeViewModel = ThemeSettings
		val color by animateColorAsState(
			targetValue = MaterialTheme.colorScheme.primary,
			label="Primary color")

		Column {
			Text("Settings",style = MaterialTheme.typography.titleLarge, color = color)
			LabelledCheckbox(
				state = themeViewModel.darkMode.value ?: isSystemInDarkTheme(),
				onCheckedChange = { themeViewModel.darkMode.value = it }
			) {
				Text("Dark mode", color = color)
			}
			themeViewModel.availableThemes.forEach {
				LabelledRadioButton(state = themeViewModel.colorTheme.value == it,
					onClick = {themeViewModel.colorTheme.value = it}) {
					Text(it.name, color = color)
				}
			}
		}
	}
}