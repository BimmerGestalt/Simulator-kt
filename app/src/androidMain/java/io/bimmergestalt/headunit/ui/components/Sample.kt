package io.bimmergestalt.headunit.ui.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.bimmergestalt.headunit.R
import io.bimmergestalt.headunit.ui.theme.HeadunitktTheme
import io.bimmergestalt.headunit.ui.theme.Theme


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	Text(
		text = "Hello $name!",
		modifier = modifier
	)
}

data class Message(val author: String, val message: String)
@Composable
fun MessageCard(message: Message) {
	Row(modifier = Modifier.padding(all = 8.dp)) {
		Image(
			painter = painterResource(R.drawable.ic_launcher_foreground),
			contentDescription = "Contact profile picture",
			modifier = Modifier
				// Set image size to 40 dp
				.size(40.dp)
				// Clip image to be shaped as a circle
				.clip(CircleShape)
				.border(1.5.dp, Theme.colorScheme.primary, CircleShape)
		)
		Spacer(modifier = Modifier.width(8.dp))

		var isExpanded by remember { mutableStateOf(false) }
		val surfaceColor by animateColorAsState(
			if (isExpanded) Theme.colorScheme.primary else Theme.colorScheme.surface,
			label = "Color"
		)

		Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
			Text(message.author,
				color = Theme.colorScheme.secondary,
				style = Theme.typography.titleSmall
			)
			Spacer(modifier = Modifier.height(4.dp))
			Surface(
				shape = Theme.shapes.small,
				shadowElevation = 2.dp,
				color = surfaceColor,
				modifier = Modifier
					.animateContentSize()
					.padding(1.dp)) {
				Text(message.message,
					style = Theme.typography.bodyMedium,
					modifier = Modifier.padding(all = 4.dp),
					maxLines = if (isExpanded) Int.MAX_VALUE else 1,
				)
			}
		}
	}
}

@Composable
fun Conversation(modifier: Modifier, messages: List<Message>) {
	LazyColumn {
		items(messages) {
			MessageCard(message = it)
		}
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SamplePreview() {
	HeadunitktTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = Theme.colorScheme.background
		) {
//		Greeting("Android")

			Conversation(Modifier, listOf(
				Message("Lexi", "Test...Test...Test"),
				Message("Lexi", "List of Android versions:\n" +
						listOf("Android KitKat", "Android Lollipop", "Android Marshmallow",
							"Android Nougat", "Android Oreo", "Android Pie").joinToString("\n")
				),
				Message("Luther", "I think Kotlin is my favorite programing language\nIt's so much fun!")
			))

		}
	}
}