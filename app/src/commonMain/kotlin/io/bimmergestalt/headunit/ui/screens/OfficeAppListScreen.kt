package io.bimmergestalt.headunit.ui.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import headunit_kt.app.generated.resources.Res
import headunit_kt.app.generated.resources.lbl_main_office
import io.bimmergestalt.headunit.apps.calendar.CalendarApp
import io.bimmergestalt.headunit.models.AMAppsModel
import io.bimmergestalt.headunit.models.RHMIAppsModel
import io.bimmergestalt.headunit.ui.components.AppList
import io.bimmergestalt.headunit.ui.components.AppListEntry
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

object OfficeAppListScreen: HeadunitScreen {
	@OptIn(ExperimentalResourceApi::class)
	override val title: String
		@Composable
		get() = stringResource(Res.string.lbl_main_office)

	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow

		val hasPhoneApps = RHMIAppsModel.knownApps.isNotEmpty()
		if (hasPhoneApps) {
			AppList(AMAppsModel.knownApps, RHMIAppsModel.knownApps, "Addressbook")
		} else {
			// demo Calendar app
			AppListEntry(icon = null, name = "Calendar") {
				navigator.push(CalendarApp)
			}
		}
	}

}