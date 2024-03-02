package io.bimmergestalt.headunit

import io.bimmergestalt.headunit.ui.components.startOfWeekDay
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class CalendarUtilsTest {
	@Test
	fun testStartOfWeekDateSunday() {
		val sunday = LocalDate(2024, 2, 25)
		val startOfWeekMonday = sunday.startOfWeekDay(DayOfWeek.MONDAY)
		assertEquals(LocalDate(2024, 2, 19), startOfWeekMonday)
		val startOfWeekSunday = sunday.startOfWeekDay(DayOfWeek.SUNDAY)
		assertEquals(LocalDate(2024, 2, 25), startOfWeekSunday)
	}
	@Test
	fun testStartOfWeekDateTuesday() {
		val tuesday = LocalDate(2024, 2, 27)
		val startOfWeekMonday = tuesday.startOfWeekDay(DayOfWeek.MONDAY)
		assertEquals(LocalDate(2024, 2, 26), startOfWeekMonday)
		val startOfWeekSunday = tuesday.startOfWeekDay(DayOfWeek.SUNDAY)
		assertEquals(LocalDate(2024, 2, 25), startOfWeekSunday)
	}
}