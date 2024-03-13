package io.bimmergestalt.headunit.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import io.bimmergestalt.headunit.models.ThemeSettings
import io.bimmergestalt.headunit.ui.components.LabelledCheckbox
import io.bimmergestalt.headunit.ui.components.LabelledRadioButton
import io.bimmergestalt.headunit.ui.components.SectionHeader
import io.bimmergestalt.headunit.ui.theme.Theme

object SettingsScreen: HeadunitScreen {
	override val title: String
		get() = "Settings"

	@Composable
	override fun Content() {
		val color by animateColorAsState(
			targetValue = Theme.colorScheme.primary,
			label="Primary color")

		Column {
			ColorSettings(color=color)
			HorizontalDivider(color=color)
			AppearanceSettings(color=color)
		}
	}

	@Composable
	fun ColorSettings(color: Color = Theme.colorScheme.primary) {
		val themeViewModel = ThemeSettings
		LabelledCheckbox(
			state = themeViewModel.darkMode ?: isSystemInDarkTheme(),
			onCheckedChange = { themeViewModel.darkMode = it }
		) {
			Text("Dark mode", color = color)
		}
		themeViewModel.availableThemes.forEach {
			LabelledRadioButton(state = themeViewModel.colorTheme == it,
				onClick = {themeViewModel.colorTheme = it}) {
				Text(it.name, color = color)
			}
		}
	}

	@Composable
	fun AppearanceSettings(color: Color = Theme.colorScheme.primary) {
		val themeViewModel = ThemeSettings
		SectionHeader("Appearance", color = color)
		themeViewModel.availableAppearances.forEach {
			LabelledRadioButton(state = themeViewModel.appearance == it,
				onClick = {themeViewModel.appearance = it}) {
				Text(it.name, color = color)
			}
		}
	}
}