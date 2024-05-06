package io.bimmergestalt.headunit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import headunit_kt.app.generated.resources.Res
import headunit_kt.app.generated.resources.bavaria_home_drawer
import io.bimmergestalt.headunit.ui.theme.Appearance
import io.bimmergestalt.headunit.ui.theme.Theme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun Background(navigator: Navigator) {
	when (Theme.appearance) {
		Appearance.Material -> BackgroundMaterial(navigator)
		Appearance.Bavaria -> BackgroundBavaria(navigator)
	}
}

@Composable
fun BackgroundMaterial(navigator: Navigator) { }

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BackgroundBavaria(navigator: Navigator) {
	Box {
		Image(
			painterResource(Res.drawable.bavaria_home_drawer), null,
			alignment = Alignment.TopStart,
			modifier = Modifier.requiredHeight(400.dp)
		)
	}
}
