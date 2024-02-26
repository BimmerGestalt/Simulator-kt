package io.bimmergestalt.headunit

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.bimmergestalt.headunit.bcl.ServerService
import io.bimmergestalt.headunit.models.RHMIAppsModel
import io.bimmergestalt.headunit.ui.screens.Main
import io.bimmergestalt.headunit.ui.screens.RHMIState
import io.bimmergestalt.headunit.ui.screens.Screens
import io.bimmergestalt.headunit.ui.theme.HeadunitktTheme
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
	HeadunitktTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
//		Greeting("Android")
			val navController = rememberNavController()
			NavHost(navController, startDestination = Screens.Main.route) {
				composable(route = Screens.Main.route) {
					Main(navController)
				}
				composable(route = Screens.Settings.route) {
					Text("Settings")
				}
				composable(route = Screens.RHMIState.route,
					enterTransition = {
						slideInHorizontally { it /2 }
					},
					popExitTransition = {
						slideOutHorizontally { it / 2} +
						fadeOut() +
						scaleOut(targetScale = 0.95f)
					}
					) {
					val app = RHMIAppsModel.knownApps[it.arguments?.getString(Screens.RHMIState.appId)]
					val stateId = it.arguments?.getString(Screens.RHMIState.stateId)?.toIntOrNull()
					if (app == null || stateId == null) {
						Text("Unable to find")
					} else {
						RHMIState(navController, app = app, stateId = stateId)
					}
				}
			}

			LaunchedEffectAndCollect(flow = RHMIAppsModel.incomingEvents) { incomingEvent ->
				Log.i("MainActivity", "Examining incomingEvent $incomingEvent")
				val app = RHMIAppsModel.knownApps[incomingEvent.appId]
				val event = app?.resources?.app?.events?.get(incomingEvent.eventId)
				if (event != null) {
					if (event is RHMIEvent.FocusEvent) {
						val targetState = incomingEvent.args[0]?.asEtchInt() // actually a component ID
						if (targetState != null && app.resources.app.states.containsKey(targetState)) {
							navController.navigate(Screens.RHMIState.create(app.appId, targetState))
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