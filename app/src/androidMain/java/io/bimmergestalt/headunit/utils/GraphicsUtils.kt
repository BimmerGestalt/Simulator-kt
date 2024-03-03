package io.bimmergestalt.headunit.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
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


data class BitmapTintable(val bitmap: Bitmap, val tintable: Boolean)
fun ByteArray.decodeBitmap(): BitmapTintable? {
	// try to parse as PNG
	try {
		val pngReader = PngReader(ByteArrayInputStream(this))
		if (pngReader.imgInfo.greyscale) {
			return BitmapTintable(BitmapFactory.decodeByteArray(this, 0, size).clearBlack(), true)
		}
	} catch (e: Exception) {

	}
	return try {
		BitmapTintable(BitmapFactory.decodeByteArray(this, 0, size).clearBlack(), false)
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

val INVERT_COLOR_MATRIX = floatArrayOf(
	-1f, 0f, 0f, 0f, 255f,
	0f, -1f, 0f, 0f, 255f,
	0f, 0f, -1f, 0f, 255f,
	0f, 0f, 0f, 1f, 0f
)
val INVERT_COLOR_FILTER = ColorFilter.colorMatrix(ColorMatrix(INVERT_COLOR_MATRIX))

/** based on https://stackoverflow.com/a/33382678/169035 */
fun tintFilter(color: Color, invert: Boolean): ColorFilter {
	val r = color.red
	val g = color.green
	val b = color.blue
	val a = color.alpha
	if (!invert) {
		val colorMatrix = ColorMatrix(
			floatArrayOf(
				r, 0f, 0f, 0f, 0f,
				0f, g, 0f, 0f, 0f,
				0f, 0f, b, 0f, 0f,
				0f, 0f, 0f, a, 0f,
			)
		)
		return ColorFilter.colorMatrix(colorMatrix)
	} else {
		val colorMatrix = ColorMatrix(
			floatArrayOf(
				-1f*r, 0f, 0f, 0f, 255f*r*2,
				0f, -1f*g, 0f, 0f, 255f*g*2,
				0f, 0f, -1f*b, 0f, 255f*b*2,
				0f, 0f, 0f, a, 0f,
			)
		)
		return ColorFilter.colorMatrix(colorMatrix)
	}
}
