package io.bimmergestalt.headunit.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavController
import io.bimmergestalt.headunit.models.RHMIAppInfo
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RHMIState(navController: NavController, app: RHMIAppInfo, stateId: Int) {
	val state = app.resources.app.states[stateId]
	if (state == null) {
		navController.popBackStack()
		return
	}

	DisposableEffect(LocalLifecycleOwner.current) {
		app.eventHandler(stateId, 1, mapOf(4 to true))  // focus
		app.eventHandler(stateId, 11, mapOf(23 to true)) // visible

		onDispose {
			app.eventHandler(stateId, 1, mapOf(4 to false))  // focus
			app.eventHandler(stateId, 11, mapOf(23 to false)) // visible
		}
	}

	val scope: CoroutineScope = rememberCoroutineScope()
	Scaffold(
		topBar = {
			val title = "${state.id} ${state.getTextModel()?.asRaDataModel()?.value}"
			TopAppBar(
				title = { Text(title) },
				navigationIcon = { IconButton(onClick = {
					navController.popBackStack()
				}){
					Icon(Icons.Filled.ArrowBack, contentDescription=null)
				} }
			)
		}
	) { padding ->
		Column(modifier = Modifier.padding(padding)) {
			Text("Yay")
		}
	}
}