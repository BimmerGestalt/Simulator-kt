package io.bimmergestalt.headunit.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIComponent
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel
import kotlinx.coroutines.launch

data class InputStateViewModel(
	val onInput: suspend (letter: String) -> Unit,
	val onAction: suspend (input: String) -> Unit,
	val onSuggestion: suspend (input: String) -> Unit,
	val result: String,
	val suggestions: List<String>
)

fun RHMIComponent.Input.viewModel(actionHandler: suspend (RHMIAction?, extra: Map<Int, Any>?) -> Unit): InputStateViewModel {
	val result = this.getResultModel()?.asRaDataModel()?.value ?: ""
	val suggestionsModel = this.getSuggestModel()?.value ?: RHMIModel.RaListModel.RHMIListConcrete(1)
	val suggestions = suggestionsModel.getWindow(0, suggestionsModel.endIndex).map { it[0].toString() }
	return InputStateViewModel(
		onInput = { actionHandler(this.getAction(), mapOf(8 to it)) },
		onAction = { actionHandler(this.getResultAction(), emptyMap()) },
		onSuggestion = { actionHandler(this.getSuggestAction(), mapOf(1 to suggestions.indexOf(it))) },
		result = result,
		suggestions = suggestions
	)
}

@Composable
fun RHMIInputState(modifier: Modifier, state: InputStateViewModel) {
	val scope = rememberCoroutineScope()

	var result by remember { mutableStateOf(state.result) }

	Box(modifier=modifier) {
		Column(modifier = Modifier.padding(4.dp)) {
			Row {
				TextField(value = result, modifier = Modifier.padding(4.dp), onValueChange = { newText ->
					val previous = result
					result = newText
					scope.launch {
						if (newText.isEmpty()) {
							state.onInput("delall")
						} else if (previous.startsWith(newText)) {
							repeat(previous.length - newText.length) {
								state.onInput("del")
							}
						} else if (newText.startsWith(previous)) {
							state.onInput(newText.substring(previous.length))
						} else {
							state.onInput("delall")
							state.onInput(newText)
						}
					}
				})
				Button(onClick = { scope.launch {
					state.onAction(result)
				} }) {
					Text("OK")
				}
			}
			LazyColumn(modifier = Modifier.padding(4.dp)) {
				items(state.suggestions) { item ->
					Text(item, modifier = Modifier.padding(8.dp).clickable { scope.launch {
						state.onSuggestion(item)
					} })
				}
			}
		}
	}
}

/*
@Preview
@Composable
fun Preview() {
	val state = InputStateViewModel(
		onInput = {},
		onAction = {},
		onSuggestion = {},
		result = "inprogressInput",
		suggestions = listOf("Result1", "result4")
	)
	RHMIInputState(modifier = Modifier, state = state)
}
 */