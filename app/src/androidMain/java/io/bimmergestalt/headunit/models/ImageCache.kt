package io.bimmergestalt.headunit.models

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asImageBitmap
import io.bimmergestalt.headunit.utils.decodeImage
import io.github.reactivecircus.cache4k.Cache
import java.security.MessageDigest

object ImageCache {
	private val cache = Cache.Builder<Long, ImageTintable>().build()
	private val empty = ImageTintable(ImageBitmap(1, 1, ImageBitmapConfig.Rgb565), false)
	/**
	 * storeInCache should be set for ImageDB images
	 */
	suspend fun decodeImageBitmap(data: ByteArray, storeInCache: Boolean = false): ImageTintable? {
		if (data.size > 75000) {
			println("ImageCache ignoring too big ${data.size}")
			return data.decodeImage()
		}
		val hash = MessageDigest.getInstance("MD5").digest(data)
		val key = (hash[0].toLong() and 0xff shl 32+24) or
				(hash[1].toLong() and 0xff shl 32+16) or
				(hash[2].toLong() and 0xff shl 32+8) or
				(hash[3].toLong() and 0xff shl 32) or
				(hash[4].toLong() and 0xff shl 24) or
				(hash[5].toLong() and 0xff shl 16) or
				(hash[6].toLong() and 0xff shl 8) or
				(hash[7].toLong() and 0xff)
		if (cache.get(key) != null) {
			println("ImageCache hit!")
		} else {
			println("ImageCache miss")
		}
		return if (storeInCache) {
			cache.get(key) {
				data.decodeImage() ?: empty
			}
		} else {
			data.decodeImage() ?: empty
		}
	}
}