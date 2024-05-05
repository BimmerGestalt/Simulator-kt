package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.headunit.ui.theme.Theme
import io.bimmergestalt.headunit.utils.loadImage
import io.bimmergestalt.headunit.utils.tintFilter
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel

@Composable
fun ImageModel(model: RHMIModel?, modifier: Modifier = Modifier) {
	val image = loadImage(model)
	if (image is ImageTintable) {
		ImageBitmapNullable(image = image, contentDescription = null, modifier = modifier)
	} else {
		Box(modifier = modifier)
	}
}

@Composable
fun ImageCell(data: Any?, modifier: Modifier = Modifier) {
	if (data is ImageTintable) {
		ImageBitmapNullable(image = data, contentDescription = null, modifier = modifier)
	}
}

@Composable
fun ImageBitmapNullable(image: ImageTintable?, contentDescription: String?, modifier: Modifier = Modifier) {
	if (image != null) {
		Image(image.image, contentDescription,
			modifier = modifier,
			colorFilter = if (image.tintable) tintFilter(Theme.colorScheme.primary, !isSystemInDarkTheme()) else null)
	} else {
		Box(modifier = modifier)
	}
}