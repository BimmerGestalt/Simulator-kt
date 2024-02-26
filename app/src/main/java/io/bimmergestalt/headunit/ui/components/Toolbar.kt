package io.bimmergestalt.headunit.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIState
import kotlinx.coroutines.launch

@Composable
fun ToolbarDrawerSheet(app: RHMIAppInfo, state: RHMIState.ToolbarState, drawerState: DrawerState, onClickAction: suspend (RHMIAppInfo, RHMIAction?) -> Unit) {
	val scope = rememberCoroutineScope()
	ModalDrawerSheet {
		NavigationDrawerItem(
			label = { Text("Close") },
			icon = {  Icon(Icons.Filled.ArrowBack, contentDescription = null) },
			selected = false,
			onClick = { scope.launch { drawerState.close() } }
		)
		Divider()
		state.toolbarComponentsList.forEach {

			NavigationDrawerItem(
				label = { TextModel(app = app, model = it.getTooltipModel()) },
				icon = {  ImageModel(app = app, model = it.getImageModel())},
				selected = false,
				onClick = { scope.launch { onClickAction(app, it.getAction()) } }
			)
		}
	}
}