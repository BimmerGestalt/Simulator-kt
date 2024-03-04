package io.bimmergestalt.headunit.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// https://www.canva.com/colors/color-palettes/mermaid-lagoon/
val MidnightBlue = Color(0xFF145da0)
val DarkBlue = Color(0xFF0c2d48)
val DarkBlueDesaturated = Color(0xFF223b4d)
val Blue = Color(0xFF2e8bc0)
val BabyBlue = Color(0xFFb1d4e0)

// https://www.canva.com/colors/color-palettes/healthy-leaves/
val OliveGreen = Color(0xFF3d550c)
val LimeGreen = Color(0xFF81b622)
val YellowGreen = Color(0xFFecf87f)
val Green = Color(0xFF59981a)

data class ColorTheme(val name: String, val light: ColorScheme, val dark: ColorScheme) {
	companion object {
		val Dynamic = ColorTheme("Dynamic", lightColorScheme(), darkColorScheme())
		val Pink = ColorTheme("Pink", lightColorScheme(
			primary = Purple40,
			secondary = PurpleGrey40,
			tertiary = Pink40,
		), darkColorScheme(
			primary = Purple80,
			secondary = PurpleGrey80,
			tertiary = Pink80,
		))
		val Lagoon = ColorTheme("Lagoon", lightColorScheme(
			primary = MidnightBlue,
			secondary = DarkBlue,
			tertiary = Blue,
		), darkColorScheme(
			primary = Blue,
			secondary = DarkBlue,
			tertiary = MidnightBlue,
		))
		val Leaves = ColorTheme("Leaves", lightColorScheme(
			primary = OliveGreen,
			secondary = Green,
			tertiary = LimeGreen,
		), darkColorScheme(
			primary = LimeGreen,
			secondary = YellowGreen,
			tertiary = Green,
		))

	}
}

/* Other default colors to override
background = Color(0xFFFFFBFE),
surface = Color(0xFFFFFBFE),
onPrimary = Color.White,
onSecondary = Color.White,
onTertiary = Color.White,
onBackground = Color(0xFF1C1B1F),
onSurface = Color(0xFF1C1B1F),
*/