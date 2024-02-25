package io.bimmergestalt.headunit.models

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateMapOf

data class AMAppInfo (
	val handle: Long,
	val appId: String,
	val name: String,
	val icon: Bitmap,
	val category: String,
	val onClick: () -> Unit
)
interface AMApps {
	val knownApps: MutableMap<String, AMAppInfo>
}
object AMAppsModel: AMApps {
	override val knownApps = mutableStateMapOf<String, AMAppInfo>()
}