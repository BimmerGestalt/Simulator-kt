package io.bimmergestalt.headunit.ui.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.screens.HeadunitScreen
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames

class RHMIScreen(val app: RHMIAppInfo, val stateId: Int): HeadunitScreen {

	private val state = app.resources.app.states[stateId]

	@Composable
	override fun Content() {
		if (state == null) {
			LocalNavigator.currentOrThrow.pop()
			return
		}
		RHMIState(app, stateId)
	}

	override val title: String
		get() {
			return if (state is RHMIState.CalendarMonthState) {
				val dateInt = state.getDateModel()?.asRaIntModel()?.value ?: 0
				dateInt.fromRhmiDate()
				LocalDate.Format {
					monthName(MonthNames.ENGLISH_FULL)
					chars(", ")
					year()
				}.format(dateInt.fromRhmiDate())
			} else {
				state?.getTextModel()?.asRaDataModel()?.value ?: "null"
			}
		}
}