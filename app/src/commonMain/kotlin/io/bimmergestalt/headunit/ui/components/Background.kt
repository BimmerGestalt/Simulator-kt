package io.bimmergestalt.headunit.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import headunit_kt.app.generated.resources.Res
import headunit_kt.app.generated.resources.bavaria_home_drawer
import headunit_kt.app.generated.resources.bavaria_title_glow
import headunit_kt.app.generated.resources.bavaria_tray
import io.bimmergestalt.headunit.screens.HeadunitScreen
import io.bimmergestalt.headunit.ui.theme.Appearance
import io.bimmergestalt.headunit.ui.theme.Theme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
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
