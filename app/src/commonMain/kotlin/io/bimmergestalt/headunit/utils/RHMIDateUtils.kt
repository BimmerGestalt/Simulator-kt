package io.bimmergestalt.headunit.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.math.max

object RHMIDateUtils {
	fun convertFromRhmiDate(date: Int): LocalDate {
		val year = (date shr 16) and 0xffff
		val month = (date shr 8) and 0xff
		val day = (date shr 0) and 0xff
		return LocalDate(year, max(1, month), max(1, day))
	}
	fun convertToRhmiDate(date: LocalDate): Int {
		return (date.year shl 16) +
				(date.monthNumber shl 8) +
				date.dayOfMonth
	}

	fun convertFromRhmiTime(time: Int): LocalTime {
		val second = (time shr 16) and 0xff
		val minute = (time shr 8) and 0xff
		val hour = (time shr 0) and 0xff
		return LocalTime(hour, minute, second)
	}
	fun convertToRhmiTime(time: LocalTime): Int {
		return (time.second shl 16) +
				(time.minute shl 8) +
				time.hour
	}
}