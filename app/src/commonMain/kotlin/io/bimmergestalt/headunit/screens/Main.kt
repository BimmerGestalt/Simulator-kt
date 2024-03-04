package io.bimmergestalt.headunit.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

object MainScreen: Screen {
	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		Column {
			Text("It works!", color = MaterialTheme.colorScheme.onBackground)
			Button(onClick = {navigator.push(SettingsScreen)}) {
				Text("Settings", color = MaterialTheme.colorScheme.onBackground)
			}
		}
	}
}