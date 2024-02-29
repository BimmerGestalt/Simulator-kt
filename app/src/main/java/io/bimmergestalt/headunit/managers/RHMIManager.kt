package io.bimmergestalt.headunit.managers

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import de.bmw.idrive.BMWRemoting
import de.bmw.idrive.BMWRemoting.RHMIDataTable
import de.bmw.idrive.BMWRemoting.RHMIResourceType
import de.bmw.idrive.BMWRemotingClient
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.models.RHMIApps
import io.bimmergestalt.headunit.models.RHMIEvent
import io.bimmergestalt.headunit.rhmi.RHMIResources
import io.bimmergestalt.headunit.utils.asEtchIntOrAny
import io.bimmergestalt.headunit.utils.isSameSize
import io.bimmergestalt.headunit.utils.merge
import io.bimmergestalt.headunit.utils.overlaps
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.util.Collections.max
import java.util.concurrent.ConcurrentHashMap

class RHMIManager(val state: RHMIApps) {
	private val TAG = "RHMIManager"
	private val actionHandlers = ConcurrentHashMap<Int, BMWRemotingClient>()
	private val eventHandlers = ConcurrentHashMap<Int, BMWRemotingClient>()
	private val pendingActions = ConcurrentHashMap<Int, ConcurrentHashMap<Int, CompletableDeferred<Boolean>>>()

	private val ioThread = HandlerThread("toEtchClients").apply { start() }
	private val ioHandler = Handler(ioThread.looper)

	fun registerApp(handle: Int, appId: String, resourceData: Map<RHMIResourceType, List<ByteArray>>) {
		val existing = state.knownApps[appId]
		if (existing != null) {
			throw BMWRemoting.IllegalArgumentException(-1, "RHMI App already registered")
		}

		Log.w(TAG, "Parsing $appId resources")
		val resources = try {
			RHMIResources.loadResources(resourceData)
		} catch (e: Exception) {
			Log.w(TAG, "With $appId resources $resourceData")
			Log.e(TAG, "Error parsing resources for $appId", e)
			throw BMWRemoting.IllegalArgumentException(-1, "Error parsing resources: $e")
		}
		val actionHandler: (Int, Map<*, *>) -> Deferred<Boolean> = { actionId, args ->
			onActionEvent(appId, actionId, args)
		}
		val eventHandler: (Int, Int, Map<*, *>) -> Unit = { componentId, eventId, args ->
			onHmiEvent(appId, componentId, eventId, args)
		}
		val appInfo = RHMIAppInfo(handle, appId, resources, actionHandler, eventHandler)
		state.knownApps[appId] = appInfo
	}

	fun unregisterAppsByHandle(handle: Int) {
		val appIds = state.knownApps.values.filter {
			it.handle == handle
		}.map {
			it.appId
		}
		appIds.forEach {
			unregisterApp(it)
		}
	}

	fun unregisterApp(appId: String) {
		state.knownApps.remove(appId)
	}

	fun addActionHandler(handle: Int, client: BMWRemotingClient) {
		actionHandlers[handle] = client
	}
	fun removeActionHandler(handle: Int) {
		actionHandlers.remove(handle)
	}

	fun addEventHandler(handle: Int, client: BMWRemotingClient) {
		eventHandlers[handle] = client
	}
	fun removeEventHandler(handle: Int) {
		eventHandlers.remove(handle)
	}

	fun setData(appId: String, modelId: Int, value: Any?) {
		if (value is RHMIDataTable) {
			value.numRows = value.data.size // apps can lie about numRows and numCols
			if (value.data.isNotEmpty()) {
				value.numColumns = max(value.data.map { it.size })
			}
			val existing = state.knownApps[appId]?.resources?.app?.getModel(modelId)
			if (existing is RHMIDataTable && existing.isSameSize(value) && !value.overlaps(existing)) {
				try {
					// create a new object to trigger state tracking
					val replacement = RHMIDataTable(existing.data, existing.virtualTableEnable,
						existing.fromRow, existing.numRows, existing.totalRows,
						existing.fromColumn, existing.numColumns, existing.totalColumns)
					replacement.merge(value)
					state.knownApps[appId]?.resources?.app?.setModel(modelId, replacement)
					return
				} catch (e: IllegalArgumentException) {
					// unable to merge this table update, just replace like normal
				}
			}
		}
		state.knownApps[appId]?.resources?.app?.setModel(modelId, value?.asEtchIntOrAny())
	}
	fun setProperty(appId: String, componentId: Int, propertyId: Int, value: Any?) {
		state.knownApps[appId]?.resources?.app?.setProperty(componentId, propertyId, value)
	}
	fun triggerEvent(appId: String, eventId: Int, args: Map<Int, Any?>) {
		Log.i(TAG, "Triggering event $appId $eventId")
		state.incomingEvents.tryEmit(RHMIEvent(appId, eventId, args))
	}

	fun onActionEvent(appId: String, actionId: Int, args: Map<*, *>): Deferred<Boolean> {
		val result = CompletableDeferred<Boolean>()
		val existing = state.knownApps[appId]
		if (existing != null) {
			ioHandler.post {
				actionHandlers[existing.handle]?.rhmi_onActionEvent(existing.handle, "", actionId, args)
			}

			if (!pendingActions.containsKey(existing.handle)) {
				pendingActions[existing.handle] = ConcurrentHashMap()
			}
			pendingActions[existing.handle]?.get(actionId)?.complete(false)
			pendingActions[existing.handle]?.put(actionId, result)
		} else {
			result.complete(false)
		}
		return result
	}

	fun onHmiEvent(appId: String, componentId: Int, eventId: Int, args: Map<*, *>) {
		val existing = state.knownApps[appId]
		if (existing != null) {
			ioHandler.post {
				actionHandlers[existing.handle]?.rhmi_onHmiEvent(existing.handle, "", componentId, eventId, args)
			}
		}
	}

	fun ackActionEvent(appId: String, actionId: Int, success: Boolean) {
		val existing = state.knownApps[appId]
		if (existing != null) {
			val deferred = pendingActions[existing.handle]?.remove(actionId)
			deferred?.complete(success)
		}
	}
}