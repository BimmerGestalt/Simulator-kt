package io.bimmergestalt.headunit.models

import androidx.compose.runtime.mutableStateMapOf
import io.bimmergestalt.headunit.rhmi.RHMIResources
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableSharedFlow

data class RHMIAppInfo (
	val handle: Int,
	val appId: String,
	val resources: RHMIResources,
	val actionHandler: (actionId: Int, args: Map<Int, Any>) -> Deferred<Boolean>,
	val eventHandler: (componentId: Int, eventId: Int, args: Map<*, *>) -> Unit,
)
data class RHMIEvent (
	val appId: String,
	val eventId: Int,
	val args: Map<Int, Any?>,
)
interface RHMIApps {
	val knownApps: MutableMap<String, RHMIAppInfo>
	val incomingEvents: MutableSharedFlow<RHMIEvent>
}
object RHMIAppsModel: RHMIApps {
	override val knownApps = mutableStateMapOf<String, RHMIAppInfo>()

	override val incomingEvents = MutableSharedFlow<RHMIEvent>(extraBufferCapacity = 1)
}