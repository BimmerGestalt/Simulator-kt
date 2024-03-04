package io.bimmergestalt.headunit
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io.bimmergestalt.headunit.models.ThemeSettings
import io.bimmergestalt.headunit.screens.MainScreen
import io.bimmergestalt.headunit.screens.TitledScreen
import io.bimmergestalt.headunit.ui.theme.HeadunitktTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	CanvasBasedWindow(canvasElementId = "ComposeTarget") {
		Contents()
	}
}

@Composable
fun Contents() {
	val themeViewModel = ThemeSettings
	HeadunitktTheme(colorTheme = themeViewModel.colorTheme.value,
		darkTheme = themeViewModel.darkMode.value) {
		val background by animateColorAsState(
			targetValue = MaterialTheme.colorScheme.background,
			label="Background color")
		Surface(modifier = Modifier.fillMaxSize(),
			color = background) {
			Navigator(MainScreen) { navigator ->
				SlideTransition(navigator) { screen ->
					Scaffold(topBar = {
						Row(verticalAlignment = Alignment.CenterVertically) {
							IconButton(onClick = {navigator.pop()}, enabled = navigator.canPop) {
								Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
							}
							if (screen is TitledScreen) {
								Text(screen.title, modifier = Modifier.padding(6.dp, 6.dp),
									color=MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium)
							}
						}
					}) { padding ->
						Box(modifier = Modifier.padding(padding).padding(8.dp)) {
							screen.Content()
						}
					}
				}
			}
		}
	}
}