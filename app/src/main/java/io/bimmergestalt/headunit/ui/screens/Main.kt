package io.bimmergestalt.headunit.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import io.bimmergestalt.headunit.bcl.ServerService
import io.bimmergestalt.headunit.models.AMAppsModel
import io.bimmergestalt.headunit.models.RHMIAppsModel
import io.bimmergestalt.headunit.ui.components.AppList
import io.bimmergestalt.headunit.ui.components.LabelledCheckbox
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(navController: NavController) {

	val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
	val scope = rememberCoroutineScope()

	BackHandler(enabled = drawerState.isOpen) {
		scope.launch { drawerState.close() }
	}
	ModalNavigationDrawer(
		drawerState = drawerState,
		drawerContent = { ModalDrawerSheet {

			NavigationDrawerItem(
				label = { Text("Close") },
				icon = {  Icon(Icons.Filled.ArrowBack, contentDescription = null) },
				selected = false,
				onClick = { scope.launch { drawerState.close() } }
			)
			Divider()
			NavigationDrawerItem(
				label = { Text("Item") },
				selected = false,
				onClick = { /*TODO*/ })

			NavigationDrawerItem(
				label = { Text("Settings") },
				selected = false,
				onClick = { navController.navigate(Screens.Settings.route) })

			val context = LocalContext.current
			val serverState = ServerService.active.collectAsState()
			val serverToggle: (Boolean) -> Unit = {
				if (it) {
					ServerService.startService(context)
				} else {
					ServerService.stopService(context)
				}
			}
			LabelledCheckbox(state = serverState.value, onCheckedChange = serverToggle) {
				Text("Listen for Apps")
			}
		} }
	) {
		Scaffold(
			topBar = {
				TopAppBar(
					title = { Text("Test") },
					navigationIcon = { IconButton(onClick = {
						scope.launch {
							drawerState.apply { if (isClosed) open() else close() }
						}
					}){
						Icon(Icons.Filled.Menu, contentDescription=null)
					} }
				)
			}
		) { padding ->
			Column(modifier = Modifier.padding(padding)) {
				AppList(navController = navController, amApps = AMAppsModel.knownApps, rhmiApps = RHMIAppsModel.knownApps)
			}
		}
	}
}