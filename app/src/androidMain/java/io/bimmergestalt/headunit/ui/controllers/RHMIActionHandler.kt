package io.bimmergestalt.headunit.ui.controllers

import android.util.Log
import androidx.navigation.NavController
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.ui.screens.Screens
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction

/**
 * Currying!
 */
fun onClickAction(navController: NavController, app: RHMIAppInfo, forceAwait: Boolean = false): suspend (RHMIAction?, extra: Map<Int, Any>?) -> Unit {
	return { action, args ->
		if (action != null) {
			Log.i("ClickAction", "Clicking action $app $action")
			val raAction = action.asRAAction()
			if (raAction != null) {
				val result = app.actionHandler(raAction.id, args ?: emptyMap())
				if (forceAwait || (action is RHMIAction.CombinedAction && action.sync.toBoolean())) {
					result.await()
				}
			}
			val hmiAction = action.asHMIAction()
			val targetState = hmiAction?.getTargetState()
			if (targetState != null) {
				navController.navigate(Screens.RHMIState.create(app.appId, targetState.id))
			}
		}
	}
}
