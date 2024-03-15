package io.bimmergestalt.headunit.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import headunit_kt.app.generated.resources.Res
import headunit_kt.app.generated.resources.lbl_main_office
import io.bimmergestalt.headunit.apps.calendar.CalendarApp
import io.bimmergestalt.headunit.ui.components.AppListEntry
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

object OfficeScreen: HeadunitScreen {
	@OptIn(ExperimentalResourceApi::class)
	override val title: String
		@Composable
		get() = stringResource(Res.string.lbl_main_office)

	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		AppListEntry(icon = null, name = "Calendar") {
			navigator.push(CalendarApp)
		}
	}

}