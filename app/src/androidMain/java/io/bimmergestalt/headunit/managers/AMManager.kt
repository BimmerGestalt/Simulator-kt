package io.bimmergestalt.headunit.managers

import android.os.Handler
import android.os.HandlerThread
import de.bmw.idrive.BMWRemoting
import de.bmw.idrive.BMWRemotingClient
import java.util.concurrent.ConcurrentHashMap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import io.bimmergestalt.headunit.models.AMAppInfo
import io.bimmergestalt.headunit.models.AMApps
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.headunit.utils.decodeAndCacheImage
import kotlinx.coroutines.runBlocking

class AMManager(val state: AMApps) {
	private val TAG = "AMManager"
	private val eventHandlers = ConcurrentHashMap<Int, BMWRemotingClient>()

	private val ioThread = HandlerThread("toEtchClients").apply { start() }
	private val ioHandler = Handler(ioThread.looper)

	fun registerApp(handle: Int, appId: String, name: String, iconData: ByteArray, category: String) {
		val existing = state.knownApps[appId]
		if (existing != null && existing.handle != handle.toLong()) {
			throw BMWRemoting.IllegalArgumentException(-1, "Incorrect AM handle")
		}
		if (existing != null && existing.category != category) {
			throw BMWRemoting.IllegalArgumentException(-1, "AM AppId already registered")
		}
		Log.i(TAG, "Starting to parse appInfo for $appId")
		val icon = runBlocking {
			iconData.decodeAndCacheImage(true) ?: ImageTintable(ImageBitmap(8, 48, ImageBitmapConfig.Rgb565), false)
		}
		val appInfo = AMAppInfo(handle.toLong(), appId, name, icon, category) {
			onAppEvent(appId)
		}
		Log.i(TAG, "Adding $appInfo")
		state.knownApps[appId] = appInfo
	}

	fun unregisterAppsByHandle(handle: Int) {
		val appIds = state.knownApps.values.filter {
			it.handle == handle.toLong()
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

	fun addEventHandler(handle: Int, client: BMWRemotingClient) {
		eventHandlers[handle] = client
	}
	fun removeEventHandler(handle: Int) {
		eventHandlers.remove(handle)
	}
	fun onAppEvent(appId: String) {
		val appInfo = state.knownApps[appId]
		if (appInfo == null) {
			Log.e(TAG, "onAppEvent can't find app id $appId")
			return
		}
		val handler = eventHandlers[appInfo.handle.toInt()]
		if (handler == null) {
			Log.w(TAG, "onAppEvent doesn't know event handler for $appId")
			return
		}
		ioHandler.post {
			handler.am_onAppEvent(appInfo.handle.toInt(), "", appInfo.appId, BMWRemoting.AMEvent.AM_APP_START)
		}
	}
}