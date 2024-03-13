package io.bimmergestalt.headunit.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

enum class Appearance() {
	Material,
	Bavaria,
}


val LocalAppearance = staticCompositionLocalOf { Appearance.Material }