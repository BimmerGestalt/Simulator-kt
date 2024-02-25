package io.bimmergestalt.headunit.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.blue
import androidx.core.graphics.get
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.set

fun ByteArray.decodeBitmap(): Bitmap? {
	// TODO: use a pool to dedupe identical icons
	return try {
		BitmapFactory.decodeByteArray(this, 0, size).clearBlack()
	} catch (e: Exception) {
		Log.w("GraphicsUtils", "Failed to decode image", e)
		null
	}
}

fun Bitmap.clearBlack(): Bitmap {
	if (this.hasAlpha()) {
		return this
	}
	val new = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
	for (x in 0 until width) {
		for (y in 0 until height) {
			val color = this[x, y]
			if (color.red < 3 && color.green < 3 && color.blue < 3) {
				new[x, y] = 0
			} else {
				new[x, y] = color
			}
		}
	}
	return new
}