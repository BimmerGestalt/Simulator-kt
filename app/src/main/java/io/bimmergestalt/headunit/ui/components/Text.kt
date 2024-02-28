package io.bimmergestalt.headunit.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.bimmergestalt.headunit.ui.screens.LocalTextDB
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel


@Composable
fun TextModel(model: RHMIModel?, modifier: Modifier = Modifier, textDB: Map<String, Map<Int, String>> = LocalTextDB.current) {
	if (model is RHMIModel.TextIdModel) {

		val dictionary = textDB["en-US"] ?: emptyMap()    // TODO use context locale
		val textId = model.textId
		val text = dictionary[textId]
		if (text != null) {
			Text(text, modifier = modifier,
				style = MaterialTheme.typography.headlineSmall,
				color = MaterialTheme.colorScheme.primary)
		} else {
			Text("", modifier = modifier)
		}
	}
	else if (model is RHMIModel.RaDataModel) {
		val text = model.value as? String
		if (text != null) {
			Text(text, modifier = modifier,
				style = MaterialTheme.typography.headlineSmall,
				color = MaterialTheme.colorScheme.primary)
		} else {
			Text("", modifier = modifier)
		}
	} else {
		Text("", modifier = modifier)
	}
}