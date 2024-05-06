package io.bimmergestalt.headunit.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.core.screen.Screen

interface HeadunitScreen: Screen {
	@get:Composable
	val title: String
}

val LocalTextDB = staticCompositionLocalOf { emptyMap<String, Map<Int, String>>() }