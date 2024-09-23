package io.bimmergestalt.headunit.apps.calendar

import androidx.compose.runtime.Composable
import io.bimmergestalt.headunit.ui.screens.HeadunitScreen
import io.bimmergestalt.headunit.ui.components.CalendarDayView
import io.bimmergestalt.headunit.ui.components.CalendarEvent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding

class CalendarAppDay(val date: LocalDate, val events: List<CalendarEvent>): HeadunitScreen {
	override val title: String
		@Composable
		get() = LocalDate.Format {
			monthName(MonthNames.ENGLISH_FULL)
			chars(" ")
			dayOfMonth(Padding.NONE)
			chars(", ")
			year()
		}.format(date)

	@Composable
	override fun Content() {
		CalendarDayView(events = events) {
			// next view not implemented
		}
	}

}