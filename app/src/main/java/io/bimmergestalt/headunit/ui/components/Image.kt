package io.bimmergestalt.headunit.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import de.bmw.idrive.BMWRemoting
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.ui.screens.LocalImageDB
import io.bimmergestalt.headunit.utils.decodeBitmap
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel

@Composable
fun ImageModel(model: RHMIModel?, modifier: Modifier = Modifier, imageDB: Map<Int, Bitmap> = LocalImageDB.current) {
	if (model is RHMIModel.ImageIdModel) {
		val imageId = model.imageId
		val image = imageDB[imageId]
		ImageBitmapNullable(bitmap = image, contentDescription = null, modifier = modifier)
	}
	else if (model is RHMIModel.RaImageModel) {
		val image = model.value
		val bitmap = image?.decodeBitmap()
		ImageBitmapNullable(bitmap = bitmap, contentDescription = null, modifier = modifier)
	} else {
		Box(modifier = modifier)
	}
}

@Composable
fun ImageCell(data: Any?, modifier: Modifier = Modifier, imageDB: Map<Int, Bitmap> = LocalImageDB.current) {
	if (data is ByteArray) {
		val bitmap = data.decodeBitmap()
		ImageBitmapNullable(bitmap = bitmap, contentDescription = null, modifier = modifier)
	} else if (data is BMWRemoting.RHMIResourceData && data.type == BMWRemoting.RHMIResourceType.IMAGEDATA) {
		val bitmap = data.data.decodeBitmap()
		ImageBitmapNullable(bitmap = bitmap, contentDescription = null, modifier = modifier)
	} else if (data is BMWRemoting.RHMIResourceIdentifier && data.type == BMWRemoting.RHMIResourceType.IMAGEID) {
		val image = imageDB[data.id]
		ImageBitmapNullable(bitmap = image, contentDescription = null, modifier = modifier)
	}
}

@Composable
fun ImageBitmapNullable(bitmap: Bitmap?, contentDescription: String?, modifier: Modifier = Modifier) {
	if (bitmap != null) {
		Image(bitmap.asImageBitmap(), contentDescription, modifier = modifier)
	} else {
		Box(modifier = modifier)
	}
}