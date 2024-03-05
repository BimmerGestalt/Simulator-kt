package io.bimmergestalt.headunit.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix


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