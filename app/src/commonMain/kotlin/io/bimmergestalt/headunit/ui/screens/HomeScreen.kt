@file:OptIn(ExperimentalResourceApi::class)

package io.bimmergestalt.headunit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import headunit_kt.app.generated.resources.Res
import headunit_kt.app.generated.resources.*
import io.bimmergestalt.headunit.apps.calendar.CalendarApp
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.headunit.ui.components.AppListEntry
import io.bimmergestalt.headunit.ui.theme.Appearance
import io.bimmergestalt.headunit.ui.theme.Theme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
object HomeScreen: HeadunitScreen {

	override val title: String
		@Composable	get() = stringResource(Res.string.lbl_main_menu)

	@Composable
	override fun Content() {
		when (Theme.appearance) {
			Appearance.Material -> HomeScreenMaterial()
			Appearance.Bavaria -> HomeScreenBavaria()
		}
	}
}

@Composable
fun HomeScreenMaterial() {
	Column {
		Text("It works!", color = Theme.colorScheme.onBackground)

		val navigator = LocalNavigator.currentOrThrow

		Column(modifier = Modifier
			.padding(10.dp)
			.verticalScroll(rememberScrollState())) {

			val settingsIcon = ImageTintable(imageResource(resource = Res.drawable.ic_carinfo), true)
			AppListEntry(icon = settingsIcon, name = "Settings") {
				navigator.push(SettingsScreen)
			}
		}
		Button(onClick = {navigator.push(CalendarApp)}) {
			Text("Calendar", color = Theme.colorScheme.onPrimaryContainer)
		}
		Button(onClick = {navigator.push(SettingsScreen)}) {
			Text("Settings", color = Theme.colorScheme.onPrimaryContainer)
		}
	}
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun HomeScreenBavaria() {
	val entries = listOf(
		stringResource(Res.string.lbl_main_multimedia) to AppListScreen(Res.string.lbl_main_multimedia, "Multimedia"),
		stringResource(Res.string.lbl_main_radio) to AppListScreen(Res.string.lbl_main_radio, "Radio"),
		stringResource(Res.string.lbl_main_telephone) to AppListScreen(Res.string.lbl_main_telephone, "Phone"),
		stringResource(Res.string.lbl_main_navigation) to AppListScreen(Res.string.lbl_main_navigation, "Navigation"),
		stringResource(Res.string.lbl_main_office) to OfficeAppListScreen,
		stringResource(Res.string.lbl_main_connecteddrive) to AppListScreen(Res.string.lbl_main_connecteddrive, "OnlineServices"),
		stringResource(Res.string.lbl_main_vehicle_information) to AppListScreen(Res.string.lbl_main_vehicle_information, "VehicleInformation"),
		stringResource(Res.string.lbl_main_settings) to SettingsScreen,
	)
	val navigator = LocalNavigator.currentOrThrow
	val lineGradient = Brush.horizontalGradient(
		0f to Color(50, 50, 50),
		0.05f to Color(128, 128, 128),
		0.8f to Color(128, 128, 128),
		1f to Color(20, 20, 20),
//		endX = 10f, tileMode = TileMode.Repeated
	)
	Box {
		Column(modifier=Modifier.width(IntrinsicSize.Min)) {
			entries.forEach { (name, screen) ->
				val modifier = if (screen == null) Modifier else
					Modifier.clickable { navigator.push(screen) }
				Column(modifier = modifier.padding(vertical=Theme.metrics.list_row_padding)) {  // big clickable box
					Text(name, modifier = Modifier.requiredWidth(IntrinsicSize.Max), softWrap = false,
						color=Theme.colorScheme.onBackground, style = Theme.typography.labelLarge)
					Box(modifier = Modifier.background(lineGradient).height(1.dp).fillMaxWidth())

				}
			}
		}
	}
}