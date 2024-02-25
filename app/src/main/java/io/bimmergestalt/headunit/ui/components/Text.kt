package io.bimmergestalt.headunit.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel


@Composable
fun TextModel(app: RHMIAppInfo, model: RHMIModel?, modifier: Modifier = Modifier) {
	if (model is RHMIModel.TextIdModel) {

		val dictionary = app.resources.textDB["en-US"] ?: emptyMap()    // TODO use context locale
		val textId = model.textId
		val text = dictionary[textId]
		if (text != null) {
			Text(text, modifier = modifier)
		} else {
			Text("", modifier = modifier)
		}
	}
	else if (model is RHMIModel.RaDataModel) {
		val text = model.value as? String
		if (text != null) {
			Text(text, modifier = modifier)
		} else {
			Text("", modifier = modifier)
		}
	} else {
		Text("", modifier = modifier)
	}
}