package io.bimmergestalt.headunit.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.bimmergestalt.headunit.models.ThemeSettings
import io.bimmergestalt.headunit.ui.components.LabelledCheckbox
import io.bimmergestalt.headunit.ui.components.LabelledRadioButton

@Composable
fun Settings(themeViewModel: ThemeSettings) {
	Column {
		Text("Settings")
		LabelledCheckbox(
			state = themeViewModel.darkMode.value ?: isSystemInDarkTheme(),
			onCheckedChange = { themeViewModel.darkMode.value = it }
		) {
			Text("Dark mode", color = MaterialTheme.colorScheme.primary)
		}
		themeViewModel.availableThemes.forEach {
			LabelledRadioButton(state = themeViewModel.colorTheme.value == it,
				onClick = {themeViewModel.colorTheme.value = it}) {
				Text(it.name, color = MaterialTheme.colorScheme.primary)
			}
		}
	}
}