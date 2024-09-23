package io.bimmergestalt.headunit.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import io.bimmergestalt.headunit.ui.theme.Appearance
import io.bimmergestalt.headunit.ui.theme.Theme

object SettingsScreen: HeadunitScreen {
	override val title: String
		@Composable
		get() = "Settings"

	@Composable
	override fun Content() {
		val color by animateColorAsState(
			targetValue = Theme.colorScheme.onBackground,
			label="Text color")

		val themeViewModel = ThemeSettings
		Column {
			AnimatedVisibility( true || themeViewModel.appearance == Appearance.Material) {
				Column {
					ColorSettings(color = color)
					HorizontalDivider(color = color)
				}
			}
			AppearanceSettings(color=color)
		}
	}

	@Composable
	fun ColorSettings(color: Color = Theme.colorScheme.onBackground) {
		val themeViewModel = ThemeSettings
		LabelledCheckbox(
			state = themeViewModel.darkMode ?: isSystemInDarkTheme(),
			onCheckedChange = { themeViewModel.darkMode = it }
		) {
			Text("Dark mode", color = color, style = Theme.typography.labelLarge)
		}
		themeViewModel.availableThemes.forEach {
			LabelledRadioButton(state = themeViewModel.colorTheme == it,
				onClick = {themeViewModel.colorTheme = it}) {
				Text(it.name, color = color, style = Theme.typography.labelLarge)
			}
		}
	}

	@Composable
	fun AppearanceSettings(color: Color = Theme.colorScheme.onBackground) {
		val themeViewModel = ThemeSettings
		SectionHeader("Appearance", color = color)
		themeViewModel.availableAppearances.forEach {
			LabelledRadioButton(state = themeViewModel.appearance == it,
				onClick = {themeViewModel.appearance = it}) {
				Text(it.name, color = color, style = Theme.typography.labelLarge)
			}
		}
	}
}