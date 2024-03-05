package io.bimmergestalt.headunit.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.headunit.utils.tintFilter
import org.jetbrains.compose.ui.tooling.preview.Preview

class ToolbarState(isOpen: Boolean = false) {
	var isOpen by mutableStateOf(isOpen)
	fun close() {
		isOpen = false
	}
	fun open() {
		isOpen = true
	}
}
data class ToolbarEntry(val icon: ImageTintable?, val text: String, val onClick: () -> Unit)

@Composable
fun ToolbarSheet(entries: List<ToolbarEntry>, drawerState: ToolbarState = remember { ToolbarState() }, content: @Composable () -> Unit) {
	val scrollState = rememberScrollState()
	Box(Modifier.padding(start=36.dp)) {
		content()
	}
	Surface(shadowElevation = 8.dp,
			modifier = Modifier
			.animateContentSize()
			.background(MaterialTheme.colorScheme.surface)
			.widthIn(min=if (drawerState.isOpen) 200.dp else 36.dp)
			.fillMaxHeight()
			.verticalScroll(scrollState)
			.pointerInput(Unit) {
				detectHorizontalDragGestures { change, dragAmount ->
					if (dragAmount < -0) {
						drawerState.isOpen = false
					} else if (dragAmount > 0) {
						drawerState.isOpen = true
					}
				}
			}) {
		ToolbarSheetContents(entries = entries, drawerState = drawerState)
	}
}

@Composable
fun ToolbarSheetContents(entries: List<ToolbarEntry>, drawerState: ToolbarState) {
	Column(Modifier.padding(6.dp)) {
		entries.forEach { entry ->
			Row(verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.clickable { entry.onClick() }
					.padding(vertical = 4.dp)
			) {
				val iconSizeModifier = Modifier
					.padding(4.dp)
					.size(32.dp)
				if (entry.icon != null) {
					Image(entry.icon.image, null,
						modifier = iconSizeModifier,
						colorFilter = if (entry.icon.tintable) tintFilter(MaterialTheme.colorScheme.primary, !isSystemInDarkTheme()) else null)
				} else {
					Box(modifier = iconSizeModifier)
				}
				if (drawerState.isOpen) {
					Text(
						entry.text,
						modifier = Modifier.width(200.dp),
						style = MaterialTheme.typography.headlineSmall,
						color = MaterialTheme.colorScheme.primary
					)
				}
			}
		}
	}
}

@Composable
@Preview
fun PreviewToolbar() {
	val entries = listOf(
		ToolbarEntry(ImageTintable(ImageBitmap(48, 48), tintable = false), "Placeholder") {}
	)
	ToolbarSheet(entries = entries) {}
}