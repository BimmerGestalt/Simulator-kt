package io.bimmergestalt.headunit

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.bimmergestalt.headunit.bcl.ServerService
import io.bimmergestalt.headunit.models.AMAppsModel
import io.bimmergestalt.headunit.models.RHMIAppsModel
import io.bimmergestalt.headunit.ui.components.AppList
import io.bimmergestalt.headunit.ui.components.LabelledCheckbox
import io.bimmergestalt.headunit.ui.theme.HeadunitktTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			Contents()
		}
		ServerService.startService(this)
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GreetingPreview() {
	Contents()
}

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
				.border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
		)
		Spacer(modifier = Modifier.width(8.dp))

		var isExpanded by remember { mutableStateOf(false) }
		val surfaceColor by animateColorAsState(
			if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
			label = "Color"
		)

		Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
			Text(message.author,
				color = MaterialTheme.colorScheme.secondary,
				style = MaterialTheme.typography.titleSmall
			)
			Spacer(modifier = Modifier.height(4.dp))
			Surface(
				shape = MaterialTheme.shapes.small,
				shadowElevation = 2.dp,
				color = surfaceColor,
				modifier = Modifier
					.animateContentSize()
					.padding(1.dp)) {
				Text(message.message,
					style = MaterialTheme.typography.bodyMedium,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contents() {
	HeadunitktTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
//		Greeting("Android")
			val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
			val scope = rememberCoroutineScope()
			ModalNavigationDrawer(
				drawerState = drawerState,
				drawerContent = { ModalDrawerSheet {

					NavigationDrawerItem(
						label = { Text("Close") },
						icon = {  Icon(Icons.Filled.ArrowBack, contentDescription = null) },
						selected = false,
						onClick = { scope.launch { drawerState.close() } }
					)
					Divider()
					NavigationDrawerItem(
						label = { Text("Item") },
						selected = false,
						onClick = { /*TODO*/ })

//					var serverState by remember { mutableStateOf(false) }
//					val serverToggle: (Boolean) -> Unit = {
//						serverState = !serverState
//					}
					val context = LocalContext.current
					val serverState = ServerService.active.collectAsState()
					val serverToggle: (Boolean) -> Unit = {
						if (it) {
							ServerService.startService(context)
						} else {
							ServerService.stopService(context)
						}
					}
					LabelledCheckbox(state = serverState.value, onCheckedChange = serverToggle) {
						Text("Listen for Apps")
					}
				} }
			) {
				Scaffold(
					topBar = {
						TopAppBar(
							title = { Text("Test") },
							navigationIcon = { IconButton(onClick = {
								scope.launch {
									drawerState.apply { if (isClosed) open() else close() }
								}
							}){
								Icon(Icons.Filled.Menu, contentDescription=null)
							} }
						)
					}
				) { padding ->
					/*Conversation(Modifier.padding(padding), listOf(
						Message("Lexi", "Test...Test...Test"),
						Message("Lexi", "List of Android versions:\n" +
								listOf("Android KitKat", "Android Lollipop", "Android Marshmallow",
									"Android Nougat", "Android Oreo", "Android Pie").joinToString("\n")
						),
						Message("Luther", "I think Kotlin is my favorite programing language\nIt's so much fun!")
					))
					 */
					Column(modifier = Modifier.padding(padding)) {
						AppList(amApps = AMAppsModel.knownApps, rhmiApps = RHMIAppsModel.knownApps)
					}
				}
			}
		}
	}
}