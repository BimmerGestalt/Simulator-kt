package io.bimmergestalt.headunit.ui.components

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel

@Composable
fun Gauge(model: RHMIModel?, modifier: Modifier = Modifier) {
	if (model is RHMIModel.RaIntModel) {
		Slider(value = model.value.toFloat(), valueRange = 0f .. 100f, onValueChange = { }, modifier = modifier)
	}
}