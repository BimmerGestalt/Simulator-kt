package io.bimmergestalt.headunit.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.bimmergestalt.headunit.ui.screens.LocalTextDB
import io.bimmergestalt.headunit.ui.theme.Theme
import io.bimmergestalt.headunit.utils.loadText
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel


@Composable
fun TextModel(model: RHMIModel?, modifier: Modifier = Modifier, textDB: Map<String, Map<Int, String>> = LocalTextDB.current) {
	val text = loadText(model, textDB = textDB)
	Text(text, modifier = modifier,
		style = Theme.typography.headlineSmall,
		color = Theme.colorScheme.primary)
}