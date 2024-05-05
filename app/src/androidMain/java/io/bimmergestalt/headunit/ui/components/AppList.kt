package io.bimmergestalt.headunit.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.bimmergestalt.headunit.models.AMAppInfo
import io.bimmergestalt.headunit.models.AMAppsModel
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.ui.controllers.onClickAction
import io.bimmergestalt.headunit.ui.theme.Theme
import io.bimmergestalt.headunit.utils.tintFilter
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIComponent
import kotlinx.coroutines.launch

@Composable
fun AppList(amApps: Map<String, AMAppInfo>, rhmiApps: Map<String, RHMIAppInfo>) {
	Log.i("AppList", "Loading app list ${amApps.values.joinToString(",")}")
	val scope = rememberCoroutineScope()

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
	Column {
		categories.value.forEach { category ->
			Column(modifier=Modifier.width(IntrinsicSize.Min)) {
				Text(modifier = Modifier.padding(start=4.dp, top=12.dp, bottom=4.dp),
					color=Theme.colorScheme.primary,
					style = Theme.typography.headlineMedium, text=category)
				Divider(color= Theme.colorScheme.tertiary)
			}
			(knownAppsByCategory.value[category] ?: emptyList()).forEach { app ->
				key(app.appId) {
					AMAppEntry(app = app) { app.onClick() }
				}
			}
			val navigator = LocalNavigator.currentOrThrow
			(entryButtonsByCategory.value[category] ?: emptyList()).forEach { (app, entryButton) ->
				key(app.appId, entryButton.id) {
					RHMIAppEntry(app = app, entryButton = entryButton) { action ->
						scope.launch {
							onClickAction(navigator = navigator, app)(action, null)
						}
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
		Image(app.icon.image, null, modifier = Modifier
			.padding(4.dp)
			.size(32.dp),
			colorFilter = if (app.icon.tintable) tintFilter(Theme.colorScheme.primary, !isSystemInDarkTheme()) else null)
		Text(app.name, style = Theme.typography.headlineSmall, color=Theme.colorScheme.primary)
	}
}

@Composable
fun RHMIAppEntry(app: RHMIAppInfo, entryButton: RHMIComponent.EntryButton, onClickAction: (RHMIAction?) -> Unit) {
	Row(verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.clickable { onClickAction(entryButton.getAction()) }
			.fillMaxWidth()
			.padding(vertical = 4.dp)
	) {
		ImageModel(model = entryButton.getImageModel(), modifier = Modifier
			.padding(4.dp)
			.size(32.dp))

		TextModel(model = entryButton.getModel(), textDB = app.resources.textDB)
	}
}