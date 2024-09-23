package io.bimmergestalt.headunit.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import headunit_kt.app.generated.resources.Res
import headunit_kt.app.generated.resources.bavaria_title_glow
import headunit_kt.app.generated.resources.bavaria_tray
import io.bimmergestalt.headunit.ui.screens.HeadunitScreen
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
fun TopBar(navigator: Navigator, screen: Screen) {
	when (Theme.appearance) {
		Appearance.Material -> DefaultTopBar(navigator, screen)
		Appearance.Bavaria -> BavariaTopBar(navigator, screen)
	}
}

@Composable
fun DefaultTopBar(navigator: Navigator, screen: Screen) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		if (navigator.canPop) {
			IconButton(onClick = {navigator.pop()}) {
				Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
			}
		} else {
			IconButton(onClick = {}, enabled = false) {
				Icon(Icons.Filled.Home, contentDescription = null)
			}
		}
		if (screen is HeadunitScreen) {
			Text(screen.title, modifier = Modifier.padding(6.dp, 6.dp),
				color= Theme.colorScheme.onBackground, style = Theme.typography.titleMedium)
		}
	}
}

@Composable
fun BavariaTopBar(navigator: Navigator, screen: Screen) {
	// drawn in Background
	Box(modifier = Modifier.height(48.dp))
}

@Composable
fun StaticTopBar(navigator: Navigator) {
	when (Theme.appearance) {
		Appearance.Material -> {}
		Appearance.Bavaria -> BavariaStaticTopBar(navigator)
	}
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BavariaStaticTopBar(navigator: Navigator) {
	val screen = navigator.lastItem

	Box {

	Row(verticalAlignment = Alignment.CenterVertically) {
		AnimatedContent(targetState = navigator.canPop) { canPop ->
			if (canPop) {
				IconButton(onClick = {navigator.pop()}) {
					Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
				}
			} else {
				IconButton(onClick = {}, enabled = false) {
					Icon(Icons.Filled.Home, contentDescription = null)
				}
			}
		}

		if (screen is HeadunitScreen) {
			Box {
				Image(
					painterResource(Res.drawable.bavaria_title_glow), null,
					modifier = Modifier.height(32.dp),
					contentScale = ContentScale.FillHeight
				)
				AnimatedContent(screen.title) { title ->
					Text(title, modifier = Modifier.padding(start=12.dp, top=4.dp, end=50.dp, bottom=6.dp),
						softWrap = false,
						color = Theme.colorScheme.onBackground, style = Theme.typography.titleMedium)
				}
			}
		}
		Spacer(Modifier.weight(1f))
		Box {
			Image(
				painterResource(Res.drawable.bavaria_tray), null,
				modifier = Modifier.height(32.dp),
				contentScale = ContentScale.FillHeight
			)
			val now = Clock.System.now()
			val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault()).time
			val timeFormatted = LocalTime.Format {
				hour(Padding.ZERO)
				char(':')
				minute(Padding.ZERO)
			}.format(localNow)
			Text(timeFormatted, modifier = Modifier.padding(start=32.dp, top=4.dp, end=50.dp, bottom=6.dp),
				softWrap = false,
				color = Theme.colorScheme.onBackground, style = Theme.typography.titleMedium)
		}
	}
	}
}