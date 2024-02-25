package io.bimmergestalt.headunit.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.utils.decodeBitmap
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel

@Composable
fun ImageModel(app: RHMIAppInfo, model: RHMIModel?, modifier: Modifier = Modifier) {
	if (model is RHMIModel.ImageIdModel) {
		val imageId = model.imageId
		val image = app.resources.imageDB[imageId]
		if (image != null) {
			Image(image.asImageBitmap(), null, modifier = modifier)
		} else {
			Box(modifier = modifier)
		}
	}
	else if (model is RHMIModel.RaImageModel) {
		val image = model.value
		val bitmap = image?.decodeBitmap()
		if (bitmap != null) {
			Image(bitmap.asImageBitmap(), null, modifier = modifier)
		} else {
			Box(modifier = modifier)
		}
	} else {
		Box(modifier = modifier)
	}
}