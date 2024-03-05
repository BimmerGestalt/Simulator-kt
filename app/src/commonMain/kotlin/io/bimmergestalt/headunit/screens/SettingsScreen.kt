package io.bimmergestalt.headunit.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import io.bimmergestalt.headunit.models.ThemeSettings
import io.bimmergestalt.headunit.ui.components.LabelledCheckbox
import io.bimmergestalt.headunit.ui.components.LabelledRadioButton

object SettingsScreen: HeadunitScreen {
	override val title: String
		get() = "Settings"

	@Composable
	override fun Content() {
		val themeViewModel = ThemeSettings
		val color by animateColorAsState(
			targetValue = MaterialTheme.colorScheme.primary,
			label="Primary color")

		Column {
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