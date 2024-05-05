package io.bimmergestalt.headunit.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.blue
import androidx.core.graphics.get
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.set
import ar.com.hjg.pngj.PngReader
import io.bimmergestalt.headunit.models.ImageCache
import io.bimmergestalt.headunit.models.ImageTintable
import java.io.ByteArrayInputStream

suspend fun ByteArray.decodeAndCacheImage(storeInCache: Boolean = false): ImageTintable? =
	ImageCache.decodeImageBitmap(this, storeInCache)    // calls ByteArray.decodeBitmap to fill cache


fun ByteArray.decodeImage(): ImageTintable? {
	// try to parse as PNG
	try {
		val pngReader = PngReader(ByteArrayInputStream(this))
		if (pngReader.imgInfo.greyscale) {
			val bitmap = BitmapFactory.decodeByteArray(this, 0, size).clearBlack()
			val image = bitmap.asImageBitmap()
			image.prepareToDraw()
			return ImageTintable(image, true)
		}
	} catch (e: Exception) {

	}
	return try {
		val bitmap = BitmapFactory.decodeByteArray(this, 0, size).clearBlack()
		val image = bitmap.asImageBitmap()
		image.prepareToDraw()
		ImageTintable(image, false)
	} catch (e: Exception) {
		Log.w("GraphicsUtils", "Failed to decode image", e)
		null
	}
}

/**
 * Image formats that don't have alpha treat black as transparent
 * so this converts any black pixels to transparent
 * This images are also meant to be tinted by the theme
 * and are marked as isPremultiplied:false
 */
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
