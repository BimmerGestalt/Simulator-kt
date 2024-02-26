package io.bimmergestalt.headunit.utils

fun Any.asEtchIntOrAny(): Any? = (this as? Number)?.toInt() ?: this
fun Any.asEtchInt(): Int? = (this as? Number)?.toInt()