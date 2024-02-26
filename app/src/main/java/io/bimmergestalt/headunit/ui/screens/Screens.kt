package io.bimmergestalt.headunit.ui.screens

sealed class Screens(val route: String) {
	data object Main: Screens("main")
	data object Settings: Screens("settings")
	data object RHMIState: Screens("rhmi/{appId}/{stateId}") {
		val appId = "appId"
		val stateId = "stateId"
		fun create(appId: String, stateId: Int) = "rhmi/$appId/$stateId"

	}
}