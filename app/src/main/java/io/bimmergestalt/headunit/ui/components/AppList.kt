package io.bimmergestalt.headunit.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.bimmergestalt.headunit.models.AMAppInfo
import io.bimmergestalt.headunit.models.AMAppsModel
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.ui.screens.Screens
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIComponent
import kotlinx.coroutines.launch

@Composable
fun AppList(navController: NavController, amApps: Map<String, AMAppInfo>, rhmiApps: Map<String, RHMIAppInfo>) {
	Log.i("AppList", "Loading app list ${amApps.values.joinToString(",")}")
	val knownAppsByCategory = remember { derivedStateOf {
		AMAppsModel.knownApps.values
			.sortedBy { it.name }
			.groupBy { it.category }
	} }
	val entryButtonsByCategory = remember { derivedStateOf {
		rhmiApps.values
			.map { app ->
				app.resources.app.components.values
					.filterIsInstance<RHMIComponent.EntryButton>()
					.map {
						app to it
					} }
			.flatten()
			.sortedBy { it.second.applicationWeight }
			.groupBy { it.second.applicationType }
	}}
	val categories = remember { derivedStateOf {
		(knownAppsByCategory.value.keys + entryButtonsByCategory.value.keys).sorted()
	}}
	Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
		categories.value.forEach { category ->
			Text(modifier = Modifier.padding(start=4.dp, top=8.dp, bottom=4.dp),
				style = MaterialTheme.typography.headlineMedium, text=category)
			(knownAppsByCategory.value[category] ?: emptyList()).forEach { app ->
				key(app.appId) {
					AMAppEntry(app = app) { app.onClick() }
				}
			}
			(entryButtonsByCategory.value[category] ?: emptyList()).forEach { (app, entryButton) ->
				key(app.appId, entryButton.id) {
					RHMIAppEntry(app = app, entryButton = entryButton) { app, action ->
						onClickAction(navController = navController, app, action)
					}
				}
			}
		}
	}
}

@Composable
fun AMAppEntry(app: AMAppInfo, onClick: (AMAppInfo) -> Unit) {
	Row(verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.clickable { onClick(app) }
			.fillMaxWidth()
			.padding(vertical = 4.dp)
	) {
		Image(app.icon.asImageBitmap(), null, modifier = Modifier
			.padding(4.dp)
			.size(32.dp))
		Text(app.name, style = MaterialTheme.typography.headlineSmall)
	}
}

suspend fun onClickAction(navController: NavController, app: RHMIAppInfo, action: RHMIAction?) {
	action ?: return
	Log.i("ClickAction", "Clicking action $app $action")
	val raAction = action.asRAAction()
	if (raAction != null) {
		val result = app.actionHandler(raAction.id, emptyMap())
		if (action is RHMIAction.CombinedAction && action.sync.toBoolean()) {
			result.await()
		}
	}
	val hmiAction = action.asHMIAction()
	val targetState = hmiAction?.getTargetState()
	if (targetState != null) {
		navController.navigate(Screens.RHMIState.create(app.appId, targetState.id))
	}
}

@Composable
fun RHMIAppEntry(app: RHMIAppInfo, entryButton: RHMIComponent.EntryButton, onClickAction: suspend (RHMIAppInfo, RHMIAction?) -> Unit) {
	val scope = rememberCoroutineScope()
	Row(verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.clickable { scope.launch { onClickAction(app, entryButton.getAction()) } }
			.fillMaxWidth()
			.padding(vertical = 4.dp)
	) {
		ImageModel(app = app, model = entryButton.getImageModel(), modifier = Modifier
			.padding(4.dp)
			.size(32.dp))

		TextModel(app = app, model = entryButton.getModel())
	}
}