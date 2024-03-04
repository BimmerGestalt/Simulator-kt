package io.bimmergestalt.headunit.screens

import cafe.adriel.voyager.core.screen.Screen

interface TitledScreen: Screen {
	val title: String
}