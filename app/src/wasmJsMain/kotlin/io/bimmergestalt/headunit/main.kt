package io.bimmergestalt.headunit
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io.bimmergestalt.headunit.models.ThemeSettings
import io.bimmergestalt.headunit.screens.MainScreen
import io.bimmergestalt.headunit.screens.HeadunitScreen
import io.bimmergestalt.headunit.ui.theme.HeadunitktTheme
import io.bimmergestalt.headunit.ui.theme.Theme

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	CanvasBasedWindow(canvasElementId = "ComposeTarget") {
		Contents()
	}
}

@Composable
fun Contents() {
	val themeViewModel = ThemeSettings
	HeadunitktTheme(colorTheme = themeViewModel.colorTheme,
		darkTheme = themeViewModel.darkMode) {
		val background by animateColorAsState(
			targetValue = Theme.colorScheme.background,
			label="Background color")
		Surface(modifier = Modifier.fillMaxSize(),
			color = background) {
			Navigator(MainScreen) { navigator ->
				SlideTransition(navigator) { screen ->
					Scaffold(topBar = {
						Row(verticalAlignment = Alignment.CenterVertically) {
							if (navigator.canPop) {
								IconButton(onClick = {navigator.pop()}) {
									Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
								}
							} else {
								IconButton(onClick = {}, enabled = false) {
									Icon(Icons.Filled.Home, contentDescription = null)
								}
							}
							if (screen is HeadunitScreen) {
								Text(screen.title, modifier = Modifier.padding(6.dp, 6.dp),
									color=Theme.colorScheme.onBackground, style = Theme.typography.titleMedium)
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