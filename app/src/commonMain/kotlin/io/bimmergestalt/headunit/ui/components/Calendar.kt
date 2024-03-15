package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.bimmergestalt.headunit.ui.theme.ColorTheme
import io.bimmergestalt.headunit.ui.theme.HeadunitktTheme
import io.bimmergestalt.headunit.ui.theme.Theme
import io.bimmergestalt.headunit.utils.padStart
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Contains much inspiration from https://github.com/boguszpawlowski/ComposeCalendar
 */

fun LocalDate.startOfWeekDay(firstDayOfWeek: DayOfWeek): LocalDate {
	if (firstDayOfWeek.isoDayNumber <= dayOfWeek.isoDayNumber) {
		return this.minus(dayOfWeek.isoDayNumber - firstDayOfWeek.isoDayNumber, DateTimeUnit.DAY)
	} else {
		return this.minus(dayOfWeek.isoDayNumber + 7 - firstDayOfWeek.isoDayNumber, DateTimeUnit.DAY)
	}
}

@Composable
fun CalendarMonth(modifier: Modifier = Modifier, selectedDate: LocalDate, firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
                  highlightDay: (LocalDate) -> Boolean, onSelectedDate: (LocalDate) -> Unit, onClick: (LocalDate) -> Unit) {
	val startOfMonth = LocalDate(year=selectedDate.year, monthNumber = selectedDate.monthNumber, dayOfMonth = 1)
	val startOfWeek = startOfMonth.startOfWeekDay(firstDayOfWeek)
	val firstDayDisplayed = if (startOfMonth.dayOfWeek == firstDayOfWeek) {
		startOfWeek.minus(1, DateTimeUnit.WEEK) // make sure there's a blank week before
	} else {
		startOfWeek
	}
	val WEEK_DAY_NAMES = listOf("MO", "TU", "WE", "TH", "FR", "SA", "SU")

	Table(modifier = modifier, columnCount = 8, rowCount = 7,
		verticalAlignment = Alignment.Top, horizontalAlignment = Alignment.CenterHorizontally,
		afterRow = { Divider() }) { columnIndex, rowIndex ->
		if (rowIndex == 0) {
			val heading = if (columnIndex == 0) { "WK" }
			else {
				WEEK_DAY_NAMES[firstDayDisplayed.plus(columnIndex - 1, DateTimeUnit.DAY).dayOfWeek.isoDayNumber - 1]
			}
			Text(heading,
				modifier = Modifier.padding(6.dp),
				style = Theme.typography.titleMedium,
				color = Theme.colorScheme.secondary)
		} else {
			val cellDay = if (columnIndex > 0) firstDayDisplayed.plus((rowIndex - 1) * 7 + columnIndex - 1, DateTimeUnit.DAY) else null

			val backgroundColor = if (cellDay == selectedDate) {
				Theme.colorScheme.primary
			} else null
			val cellModifier = if (backgroundColor != null) {
				Modifier.background(backgroundColor)
			} else { Modifier }
			val clickableCellModifier = if (cellDay != null) {
				if (cellDay.month == selectedDate.month) {
					if (highlightDay(cellDay)) {    // day has events to click into
						cellModifier.clickable { onClick(cellDay) }
					} else {    // just change the highlight
						cellModifier.clickable { onSelectedDate(cellDay) }
					}
				} else {
					cellModifier.clickable { onSelectedDate(cellDay) }
				}
			} else { cellModifier }

			Column(modifier = clickableCellModifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
				if (columnIndex == 0) {
					val weekDay = firstDayDisplayed.plus((rowIndex - 1) * 7, DateTimeUnit.DAY)
					Text((weekDay.dayOfYear / 7 + 1).toString(),
						style = Theme.typography.titleMedium,
						color = Theme.colorScheme.secondary)
				} else if (cellDay != null) {
					val color = if (cellDay == selectedDate) {
						Theme.colorScheme.onPrimary
					} else {
						if (cellDay.month == selectedDate.month) {Theme.colorScheme.primary} else {Theme.colorScheme.secondary}
					}
					Text(cellDay.dayOfMonth.toString(),
						textAlign = TextAlign.Center,
						style = Theme.typography.titleMedium,
						color = color)
					if (highlightDay(cellDay)) {
						Image(painter = ColorPainter(Theme.colorScheme.tertiary),
							contentDescription = null,
							modifier = Modifier
								.size(8.dp, 8.dp)
								.clip(CircleShape))
					} else {
						Box(modifier = Modifier.size(8.dp, 8.dp))
					}
				}
			}
		}
	}
}

data class CalendarEvent(val start: LocalTime, val end: LocalTime, val title: String)

@Composable
fun CalendarDayView(modifier: Modifier = Modifier, events: List<CalendarEvent>, onClick: (CalendarEvent) -> Unit) {
	LazyColumn(modifier = modifier) {
		items(events) {
			Column(Modifier.padding(4.dp).clickable { onClick(it) }) {
				val startTime = "${it.start.hour.padStart(2, '0')}:${it.start.minute.padStart(2, '0')}"
				val endTime = "${it.end.hour.padStart(2, '0')}:${it.end.minute.padStart(2, '0')}"
				val time = "$startTime - $endTime"
				Text(time, modifier = Modifier.padding(4.dp),
					style = Theme.typography.bodyMedium, color = Theme.colorScheme.primary)
				Text(it.title, modifier = Modifier.padding(4.dp),
					style = Theme.typography.bodyMedium, color = Theme.colorScheme.tertiary)
			}
		}
	}
}

@Preview
@Composable
fun Preview() {

	HeadunitktTheme(materialColorTheme = ColorTheme.Lagoon,
		darkTheme = isSystemInDarkTheme()) {
		Surface {
//			CalendarMonth(
//				firstDayOfWeek = DayOfWeek.SUNDAY,
//				selectedDate = LocalDate(2024, 2, 19),
//				highlightDay = { it.dayOfYear % 9 == 0 },
//				onSelectedDate = {}) {}
			CalendarDayView(Modifier, listOf(
				CalendarEvent(LocalTime(5, 0), LocalTime(5, 30), "Coffee"),
				CalendarEvent(LocalTime(7, 0), LocalTime(8, 30), "Exercise"),
				CalendarEvent(LocalTime(6, 0), LocalTime(6, 30), "Breakfast"),
				CalendarEvent(LocalTime(10, 0), LocalTime(16, 0), "Work"),
			)) {}
		}
	}
}