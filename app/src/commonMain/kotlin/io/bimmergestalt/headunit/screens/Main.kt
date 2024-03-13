package io.bimmergestalt.headunit.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.bimmergestalt.headunit.apps.calendar.CalendarApp
import io.bimmergestalt.headunit.ui.theme.Theme

object MainScreen: Screen {
	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		Column {
			Text("It works!", color = Theme.colorScheme.onBackground)
			Button(onClick = {navigator.push(CalendarApp)}) {
				Text("Calendar", color = Theme.colorScheme.onPrimaryContainer)
			}
			Button(onClick = {navigator.push(SettingsScreen)}) {
				Text("Settings", color = Theme.colorScheme.onPrimaryContainer)
			}
		}
	}
}