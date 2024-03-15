package io.bimmergestalt.headunit

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io.bimmergestalt.headunit.bcl.ServerService
import io.bimmergestalt.headunit.models.RHMIAppsModel
import io.bimmergestalt.headunit.models.ThemeSettings
import io.bimmergestalt.headunit.screens.HeadunitScreen
import io.bimmergestalt.headunit.screens.HomeScreen
import io.bimmergestalt.headunit.ui.components.Background
import io.bimmergestalt.headunit.ui.components.StaticTopBar
import io.bimmergestalt.headunit.ui.components.TopBar
import io.bimmergestalt.headunit.ui.screens.AppListScreen
import io.bimmergestalt.headunit.ui.screens.RHMIScreen
import io.bimmergestalt.headunit.ui.theme.Appearance
import io.bimmergestalt.headunit.ui.theme.HeadunitktAndroidTheme
import io.bimmergestalt.headunit.ui.theme.Theme
import io.bimmergestalt.headunit.utils.LaunchedEffectAndCollect
import io.bimmergestalt.headunit.utils.asEtchInt
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIEvent

class MainActivity : ComponentActivity() {
	companion object {

	}
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			Contents()
		}
		ServerService.startService(this)
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppPreview() {
	Contents()
}

@Composable
fun Contents() {
	val themeViewModel = ThemeSettings
	themeViewModel.supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

	HeadunitktAndroidTheme(colorTheme = themeViewModel.colorTheme,
		darkTheme = themeViewModel.darkMode ?: isSystemInDarkTheme()) {
		val background by animateColorAsState(
			targetValue = Theme.colorScheme.background,
			label="Background color")
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = background
		) {
//		Greeting("Android")
			val homeScreen = if (Theme.appearance == Appearance.Material) AppListScreen else HomeScreen
			Navigator(homeScreen) { navigator ->
				Background(navigator)
				SlideTransition(navigator) { screen ->
					Scaffold(containerColor = Color.Transparent, topBar = { TopBar(navigator, screen) }) { padding ->
						val appearancePadding = when(Theme.appearance) {
							Appearance.Material -> padding
							Appearance.Bavaria -> PaddingValues(start=100.dp, top = padding.calculateTopPadding())
						}
						Box(modifier = Modifier.padding(appearancePadding)) {
							screen.Content()
						}
					}
				}
				StaticTopBar(navigator = navigator)

				LaunchedEffectAndCollect(flow = RHMIAppsModel.incomingEvents) { incomingEvent ->
					Log.i("MainActivity", "Examining incomingEvent $incomingEvent")
					val app = RHMIAppsModel.knownApps[incomingEvent.appId]
					val event = app?.resources?.app?.events?.get(incomingEvent.eventId)
					if (event != null) {
						if (event is RHMIEvent.FocusEvent) {
							val targetState = incomingEvent.args[0]?.asEtchInt() // actually a component ID
							if (targetState != null && app.resources.app.states.containsKey(targetState)) {
								navigator.push(RHMIScreen(app, targetState))
							} else {
								Log.i("MainActivity", "Could not find app state ${app.appId}/$targetState in ${app.resources.app.states.keys}")
							}
						}
					} else {
						Log.i("MainActivity", "Could not find app event ${incomingEvent.appId}/${incomingEvent.eventId}")
					}
				}
			}
		}
	}
}