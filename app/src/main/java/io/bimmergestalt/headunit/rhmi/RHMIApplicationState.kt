package io.bimmergestalt.headunit.rhmi

import androidx.compose.runtime.mutableStateMapOf
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIApplication
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIComponent
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIEvent
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIState


class RHMIApplicationState(): RHMIApplication() {
	override val actions: MutableMap<Int, RHMIAction> = HashMap()
	override val components: MutableMap<Int, RHMIComponent> = HashMap()
	override val events: MutableMap<Int, RHMIEvent> = HashMap()
	override val models: MutableMap<Int, RHMIModel> = HashMap()
	override val states: MutableMap<Int, RHMIState> = HashMap()

	val modelStates = mutableStateMapOf<Int, Any?>()
	val propertyStates = HashMap<Int, MutableMap<Int, Any?>>().withDefault { mutableStateMapOf() }

	override fun setModel(modelId: Int, value: Any) {
		println("Setting value modelId $modelId -> $value")
		modelStates[modelId] = value
	}
	override fun getModel(modelId: Int): Any? {
		println("Loading value modelId $modelId -> ${modelStates[modelId]} ${modelStates[modelId]?.javaClass}")
		return modelStates[modelId]
	}


	override fun setProperty(componentId: Int, propertyId: Int, value: Any?) {
		propertyStates[componentId]!![propertyId] = value
	}
	override fun getProperty(componentId: Int, propertyId: Int): Any? = propertyStates[componentId]!![propertyId]

	override fun triggerHMIEvent(eventId: Int, args: Map<Any, Any?>) {
		TODO("Not yet implemented")
	}

}