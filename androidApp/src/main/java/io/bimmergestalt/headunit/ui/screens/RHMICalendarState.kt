package io.bimmergestalt.headunit.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bimmergestalt.headunit.rhmi.RHMIDateUtils
import io.bimmergestalt.headunit.ui.components.CalendarDayView
import io.bimmergestalt.headunit.ui.components.CalendarEvent
import io.bimmergestalt.headunit.ui.components.CalendarMonth
import io.bimmergestalt.headunit.utils.asEtchInt
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIComponent
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIState
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

fun LocalDate.asRhmiDate(): Int = RHMIDateUtils.convertToRhmiDate(this)
fun Int.fromRhmiDate(): LocalDate = RHMIDateUtils.convertFromRhmiDate(this)

data class CalendarMonthStateViewModel(
	val onChangeAction: suspend (date: LocalDate) -> Unit,
	val onAction: suspend (date: LocalDate) -> Unit,
	val date: LocalDate,
	val highlightList: List<LocalDate>
)

fun RHMIState.CalendarMonthState.viewModel(actionHandler: suspend (RHMIAction?, extra: Map<Int, Any>?) -> Unit): CalendarMonthStateViewModel {
	val date = this.getDateModel()?.asRaIntModel()?.value
		?.takeIf { it > LocalDate(1900, 1, 1).asRhmiDate() }?.fromRhmiDate() ?:
		Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
	val highlightListModel = this.getHighlightListModel()?.asRaListModel()?.value ?: RHMIModel.RaListModel.RHMIListConcrete(1)
	val highlightList = highlightListModel.getWindow(0, highlightListModel.endIndex).mapNotNull { dataRow ->
		dataRow.getOrNull(0)?.asEtchInt()?.let { LocalDate(date.year, date.monthNumber, it) }
	}
	return CalendarMonthStateViewModel(
		onChangeAction = { actionHandler(this.getChangeAction(), mapOf(0 to it.asRhmiDate())) },
		onAction = { actionHandler(this.getAction(), mapOf(0 to it.asRhmiDate())) },
		date = date,
		highlightList = highlightList
	)
}

@Composable
fun RHMICalendarMonthState(modifier: Modifier, state: CalendarMonthStateViewModel) {
	val scope = rememberCoroutineScope()
	var localDate by remember { mutableStateOf(state.date)}

	CalendarMonth(
		modifier = modifier,
		selectedDate = localDate,
		firstDayOfWeek = DayOfWeek.SUNDAY,
		highlightDay = { state.highlightList.contains(it) },
		onSelectedDate = {scope.launch { localDate = it; state.onChangeAction(it) }}
	) {
		scope.launch {
			localDate = it
			state.onAction(it)
		}
	}
}


fun LocalTime.asRhmiTime(): Int = RHMIDateUtils.convertToRhmiTime(this)
fun Int.fromRhmiTime(): LocalTime = RHMIDateUtils.convertFromRhmiTime(this)
data class CalendarStateViewModel(
	val date: LocalDate,
	val events: List<CalendarEvent>,
	val onAction: suspend (event: CalendarEvent?) -> Unit,
)

fun RHMIComponent.CalendarDay.viewModel(actionHandler: suspend (RHMIAction?, extra: Map<Int, Any>?) -> Unit): CalendarStateViewModel {
	val appointments = this.getAppointmentListModel()?.asRaListModel()?.value ?: RHMIModel.RaListModel.RHMIListConcrete(4)
	val events = appointments.getWindow(0, appointments.endIndex).map {
		val start = (it.getOrNull(0)?.asEtchInt() ?: 0).fromRhmiTime()
		val end = (it.getOrNull(1)?.asEtchInt() ?: 0).fromRhmiTime()
		// iconId isn't used yet
		val title = (it.getOrNull(3) as? String) ?: "null"
		CalendarEvent(start, end, title)
	}
	return CalendarStateViewModel(
		date = this.getDateModel()?.asRaIntModel()?.value?.fromRhmiDate() ?: LocalDate(1900, 1, 1),
		events = events
	) {
		actionHandler(this.getAction(), mapOf(0 to events.indexOf(it) + 1))
	}
}

@Composable
fun RHMICalendarState(modifier: Modifier, state: CalendarStateViewModel) {
	val scope = rememberCoroutineScope()

	Column(modifier = modifier) {
		val title = LocalDate.Format {
			year()
			char('-')
			monthNumber()
			char('-')
			dayOfMonth()
		}.format(state.date)
		Text(title,
			style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary,
			modifier = Modifier.padding(4.dp).clickable { scope.launch {
				state.onAction(null)
			}}
		)
		CalendarDayView(
			events = state.events
		) {
			scope.launch {
				state.onAction(it)
			}
		}
	}
}