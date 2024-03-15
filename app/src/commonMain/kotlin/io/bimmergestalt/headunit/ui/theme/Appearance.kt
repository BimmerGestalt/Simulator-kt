package io.bimmergestalt.headunit.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.staticCompositionLocalOf

enum class Appearance(val colorScheme: ColorScheme?, val metrics: Metrics) {
	Material(null, MaterialMetrics),
	Bavaria(ColorTheme.Bavaria, BavariaMetrics),
}


val LocalAppearance = staticCompositionLocalOf { Appearance.Material }