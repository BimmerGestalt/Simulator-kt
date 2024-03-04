package io.bimmergestalt.headunit.ui.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import io.bimmergestalt.headunit.models.RHMIAppInfo

class RHMIScreen(val app: RHMIAppInfo, val stateId: Int): Screen {
	@Composable
	override fun Content() {
		RHMIState(app, stateId)
	}
}