package io.bimmergestalt.headunit.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import headunit_kt.app.generated.resources.Res
import headunit_kt.app.generated.resources.ic_carinfo
import io.bimmergestalt.headunit.models.AMAppsModel
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.headunit.models.RHMIAppsModel
import io.bimmergestalt.headunit.screens.SettingsScreen
import io.bimmergestalt.headunit.ui.components.AppList
import io.bimmergestalt.headunit.ui.components.AppListEntry
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.imageResource


object AppListScreen: Screen {
	@OptIn(ExperimentalResourceApi::class)
	@Composable
	override fun Content() {

		val navigator = LocalNavigator.currentOrThrow

		Column(modifier = Modifier
			.padding(10.dp)
			.verticalScroll(rememberScrollState())) {
			AppList(amApps = AMAppsModel.knownApps, rhmiApps = RHMIAppsModel.knownApps)
			
			val settingsIcon = ImageTintable(imageResource(resource = Res.drawable.ic_carinfo), true)
			AppListEntry(icon = settingsIcon, name = "Settings") {
				navigator.push(SettingsScreen)
			}
		}
	}
}