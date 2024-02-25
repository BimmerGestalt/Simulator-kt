package io.bimmergestalt.headunit.models

import androidx.compose.runtime.mutableStateMapOf
import io.bimmergestalt.headunit.rhmi.RHMIResources

data class RHMIAppInfo (
	val handle: Int,
	val appId: String,
	val resources: RHMIResources,
	val actionHandler: suspend (actionId: Int, args: Map<Int, Any>) -> Boolean,
	val eventHandler: (componentId: Int, eventId: Int, args: Map<*, *>) -> Unit,
)
interface RHMIApps {
	val knownApps: MutableMap<String, RHMIAppInfo>
}
object RHMIAppsModel: RHMIApps {
	override val knownApps = mutableStateMapOf<String, RHMIAppInfo>()
}