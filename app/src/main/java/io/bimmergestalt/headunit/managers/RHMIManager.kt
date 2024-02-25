package io.bimmergestalt.headunit.managers

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import de.bmw.idrive.BMWRemoting
import de.bmw.idrive.BMWRemoting.RHMIDataTable
import de.bmw.idrive.BMWRemoting.RHMIResourceData
import de.bmw.idrive.BMWRemoting.RHMIResourceIdentifier
import de.bmw.idrive.BMWRemoting.RHMIResourceType
import de.bmw.idrive.BMWRemotingClient
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.models.RHMIApps
import io.bimmergestalt.headunit.rhmi.RHMIResources
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
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
		val resources = try {
			RHMIResources.loadResources(resourceData)
		} catch (e: Exception) {
			Log.w(TAG, "With $appId resources $resourceData")
			Log.e(TAG, "Error parsing resources for $appId", e)
			throw BMWRemoting.IllegalArgumentException(-1, "Error parsing resources: $e")
		}
		val actionHandler: suspend (Int, Map<*, *>) -> Boolean = { actionId, args ->
			onActionEvent(appId, actionId, args).await()
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
/*
	private fun simplifyData(value: Any?): Any? {
		return when (value) {
			is RHMIDataTable -> {
				// TODO handle partial table updates :fear:
				val data = value.data.map { row ->
					row.map { cell ->
						simplifyData(cell)
					}
				}
				RHMITableUpdate(totalRows = value.totalRows.toLong(), totalColumns = value.totalColumns.toLong(),
					startRow = value.fromRow.toLong(), startColumn = value.fromColumn.toLong(),
					numRows = value.numRows.toLong(), numColumns = value.numColumns.toLong(),
					data = data
				)
			}
			is RHMIResourceData -> value.data    // assume the destination model is RA
			is RHMIResourceIdentifier -> if (value.type == RHMIResourceType.IMAGEID) {
				RHMIImageId(value.id.toLong())
			} else if (value.type == RHMIResourceType.TEXTID) {
				RHMITextId(value.id.toLong())
			} else {
				value.id
				// assume the destination model is ID
			}
			is Boolean -> value
			is ByteArray -> value
			is Number -> value
			is String -> value
			null -> null
			else -> {
				Log.e(TAG, "Unknown data type $value")
				null
			}
		}
	}
*/
	fun setData(appId: String, modelId: Int, value: Any?) {
		// perhaps type validation should be done?
		// but then Kotlin would need to know
		// or an error callback needs to be handled from Dart
		state.knownApps[appId]?.resources?.app?.modelStates?.put(modelId, value)
	}
	fun setProperty(appId: String, componentId: Int, propertyId: Int, value: Any?) {
		state.knownApps[appId]?.resources?.app?.propertyStates?.get(componentId)?.put(propertyId, value)
//		callbacks.rhmiSetProperty(appId, componentId, propertyId, value)
	}
	fun triggerEvent(appId: String, eventId: Int, args: Map<Int, Any?>) {
//		callbacks.rhmiTriggerEvent(appId, eventId, args)
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