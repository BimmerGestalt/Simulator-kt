package io.bimmergestalt.headunit
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io.bimmergestalt.headunit.models.ThemeSettings
import io.bimmergestalt.headunit.ui.screens.HomeScreen
import io.bimmergestalt.headunit.ui.components.Background
import io.bimmergestalt.headunit.ui.components.StaticTopBar
import io.bimmergestalt.headunit.ui.components.TopBar
import io.bimmergestalt.headunit.ui.theme.Appearance
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
	HeadunitktTheme(materialColorTheme = themeViewModel.colorTheme,
		darkTheme = themeViewModel.darkMode) {
		val background by animateColorAsState(
			targetValue = Theme.colorScheme.background,
			label="Background color")
		Surface(modifier = Modifier.fillMaxSize(),
			color = background) {
			Navigator(HomeScreen) { navigator ->
				Background(navigator)
				SlideTransition(navigator) { screen ->
					Scaffold(containerColor = Color.Transparent, topBar = { TopBar(navigator, screen) }) { padding ->
						val appearancePadding = when(Theme.appearance) {
							Appearance.Material -> padding
							Appearance.Bavaria -> PaddingValues(start=100.dp, top = padding.calculateTopPadding())
						}
						Box(modifier = Modifier.padding(appearancePadding).padding(8.dp)) {
							screen.Content()
						}
					}
				}
				StaticTopBar(navigator = navigator)
			}
		}
	}
}