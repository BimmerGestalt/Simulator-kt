package io.bimmergestalt.headunit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun HeadunitktAndroidTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	// Dynamic color is available on Android 12+
	colorTheme: ColorTheme = ColorTheme.Dynamic,
	content: @Composable () -> Unit
) {
	val realColorTheme = if (colorTheme == ColorTheme.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		val context = LocalContext.current
		ColorTheme("Dynamic", dynamicLightColorScheme(context), dynamicDarkColorScheme(context))
	} else { colorTheme }

	val colorScheme = when {
		darkTheme -> colorTheme.dark
		else -> colorTheme.light
	}

	// set the titlebar
	val view = LocalView.current
	if (!view.isInEditMode) {
		SideEffect {
			val window = (view.context as Activity).window
			window.statusBarColor = colorScheme.primary.toArgb()
			WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
		}
	}

	// now apply the rest of the theme
	HeadunitktTheme(darkTheme, realColorTheme, content)
}