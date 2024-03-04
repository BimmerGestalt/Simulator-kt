package io.bimmergestalt.headunit.utils

fun Any.asEtchIntOrAny(): Any? = (this as? Number)?.toInt() ?: this
fun Any.asEtchInt(): Int? = (this as? Number)?.toInt()
fun Any.asBoolean(): Boolean {
	return when (this) {
		is String -> this.toBoolean()
		is Boolean -> this
		else -> false
	}
}

fun Int.padStart(length: Int, padChar: Char) = this.toString().padStart(length, padChar)