package io.bimmergestalt.headunit.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

interface HeadunitScreen: Screen {
	@get:Composable
	val title: String
}