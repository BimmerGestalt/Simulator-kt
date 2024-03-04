package io.bimmergestalt.headunit.ui.controllers

import android.util.Log
import cafe.adriel.voyager.navigator.Navigator
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.ui.screens.RHMIScreen
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction

/**
 * Currying!
 */
fun onClickAction(navigator: Navigator, app: RHMIAppInfo, forceAwait: Boolean = false): suspend (RHMIAction?, extra: Map<Int, Any>?) -> Unit {
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
				navigator.push(RHMIScreen(app, targetState.id))
			}
		}
	}
}
