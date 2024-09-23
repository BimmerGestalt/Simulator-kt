package io.bimmergestalt.headunit.ui.screens

import androidx.compose.runtime.Composable
import io.bimmergestalt.headunit.models.AMAppsModel
import io.bimmergestalt.headunit.models.RHMIAppsModel
import io.bimmergestalt.headunit.ui.components.AppList
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
class AppListScreen(val nameResource: StringResource, val category: String): HeadunitScreen {

	override val title: String
		@Composable
		get() = stringResource(nameResource)

	@Composable
	override fun Content() {
		AppList(AMAppsModel.knownApps, RHMIAppsModel.knownApps, category)
	}
}