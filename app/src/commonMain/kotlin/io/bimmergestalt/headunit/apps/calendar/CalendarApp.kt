package io.bimmergestalt.headunit.apps.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.bimmergestalt.headunit.screens.HeadunitScreen
import io.bimmergestalt.headunit.ui.components.CalendarEvent
import io.bimmergestalt.headunit.ui.components.CalendarMonth
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.toLocalDateTime

object CalendarApp: HeadunitScreen {
	val HOLIDAYS = mapOf(
		LocalDate(2024, 1, 1) to "New Years",
		LocalDate(2024, 1, 15) to "Martin Luther King Day",
		LocalDate(2024, 2, 14) to "Valentine's Day",
		LocalDate(2024, 2, 19) to "US Presidents' Day",
		LocalDate(2024, 3, 17) to "St. Patrick's Day",
		LocalDate(2024, 3, 19) to "Spring Equinox",
		LocalDate(2024, 3, 31) to "Easter",
		LocalDate(2024, 4, 1) to "April Fool's Day",
		LocalDate(2024, 4, 22) to "Earth Day",
		LocalDate(2024, 5, 5) to "Cinco de Mayo",
		LocalDate(2024, 5, 12) to "Mother's Day",
		LocalDate(2024, 5, 27) to "Memorial Day",
		LocalDate(2024, 6, 16) to "Father's Day",
		LocalDate(2024, 6, 19) to "Juneteenth",
		LocalDate(2024, 6, 20) to "Summer Solstice",
		LocalDate(2024, 7, 4) to "Independence Day",
		LocalDate(2024, 9, 2) to "Labor Day",
		LocalDate(2024, 9, 22) to "Fall Equinox",
		LocalDate(2024, 10, 14) to "Columbus Day",
		LocalDate(2024, 10, 31) to "Halloween",
		LocalDate(2024, 11, 11) to "Veterans Day",
		LocalDate(2024, 11, 28) to "Thanksgiving Day",
		LocalDate(2024, 11, 29) to "Black Friday",
		LocalDate(2024, 12, 6) to "St Nicholas Day",
		LocalDate(2024, 12, 25) to "Christmas Day",
		LocalDate(2024, 12, 31) to "New Years Eve",
	)

	private var selectedDate by mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)

	override val title: String
		get() = LocalDate.Format {
				monthName(MonthNames.ENGLISH_FULL)
				chars(", ")
				year()
			}.format(selectedDate)

	@Composable
	override fun Content() {
		val navigator = LocalNavigator.currentOrThrow
		CalendarMonth(selectedDate = selectedDate, firstDayOfWeek = DayOfWeek.SUNDAY,
			highlightDay = { HOLIDAYS.containsKey(it) }, onSelectedDate = { selectedDate = it}) {
			// clicked into a day
				navigator.push(
					CalendarAppDay(it, listOf(
					CalendarEvent(LocalTime(0, 0), LocalTime(0, 0), HOLIDAYS[it] ?: "")
				))
			)
			this.selectedDate = it
		}
	}
}
