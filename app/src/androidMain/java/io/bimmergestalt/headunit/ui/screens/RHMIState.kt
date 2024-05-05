package io.bimmergestalt.headunit.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.ui.components.Gauge
import io.bimmergestalt.headunit.ui.components.ImageModel
import io.bimmergestalt.headunit.ui.components.List
import io.bimmergestalt.headunit.ui.components.TextModel
import io.bimmergestalt.headunit.ui.components.ToolbarEntry
import io.bimmergestalt.headunit.ui.components.ToolbarSheet
import io.bimmergestalt.headunit.ui.components.ToolbarState
import io.bimmergestalt.headunit.ui.controllers.onClickAction
import io.bimmergestalt.headunit.utils.asBoolean
import io.bimmergestalt.headunit.utils.loadImage
import io.bimmergestalt.headunit.utils.loadText
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIComponent
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIProperty
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIState
import kotlinx.coroutines.launch

@Composable
fun RHMIState(app: RHMIAppInfo, stateId: Int) {
	val state = app.resources.app.states[stateId]
	if (state == null) {
		LocalNavigator.currentOrThrow.pop()
		return
	}

	val scope = rememberCoroutineScope()
	val onClickAction = onClickAction(LocalNavigator.currentOrThrow, app)

	DisposableEffect(Unit) {
		app.eventHandler(stateId, 1, mapOf(4 to true))  // focus
		app.eventHandler(stateId, 11, mapOf(23 to true)) // visible

		onDispose {
			app.eventHandler(stateId, 1, mapOf(4 to false))  // focus
			app.eventHandler(stateId, 11, mapOf(23 to false)) // visible
		}
	}

	CompositionLocalProvider(
		LocalTextDB provides app.resources.textDB
	) {
		val navigator = LocalNavigator.currentOrThrow
		if (state is RHMIState.ToolbarState) {
			val toolbarState = remember { ToolbarState(false) }
			BackHandler(enabled = toolbarState.isOpen) {
				scope.launch { toolbarState.close() }
			}
			val toolbarEntries = state.toolbarComponentsList.map {
				val icon = loadImage(it.getImageModel())
				val text = loadText(it.getTooltipModel(), app.resources.textDB)
				ToolbarEntry(icon, text) { scope.launch { onClickAction(it.getAction(), null) }}
			}
			ToolbarSheet(entries = toolbarEntries, drawerState = toolbarState) {
				RHMIStateBody(Modifier, app = app, state = state) { action, args -> scope.launch {
					onClickAction(action, args)
				} }
			}
		} else if (state is RHMIState.CalendarMonthState) {
			RHMICalendarMonthState(
				modifier = Modifier,
				state = state.viewModel(onClickAction)
			)
		} else if (state is RHMIState.CalendarState && state.componentsList.size == 1 && state.componentsList[0] is RHMIComponent.CalendarDay) {
			val calendarState = (state.componentsList[0] as RHMIComponent.CalendarDay).viewModel(onClickAction)
			RHMICalendarState(modifier = Modifier, state = calendarState)
		} else if (state.componentsList.size == 1 && state.componentsList[0] is RHMIComponent.Input) {
			val actionHandler = onClickAction(navigator, app, forceAwait = true)
			val inputState = (state.componentsList[0] as RHMIComponent.Input).viewModel(actionHandler)
			RHMIInputState(Modifier, inputState)
		} else {
			RHMIStateBody(Modifier, app = app, state = state) { action, args -> scope.launch {
				onClickAction(action, args)
			} }
		}
	}
}

@Composable
fun RHMIStateBody(modifier: Modifier, app: RHMIAppInfo, state: RHMIState, onClickAction: (RHMIAction?, Map<Int, Any>?) -> Unit) {
	val windowWidth = LocalConfiguration.current.screenWidthDp
	val layout = if (windowWidth > 700) 0 else 1

	val absoluteComponents = state.componentsList.filter {
		it.properties.containsKey(RHMIProperty.PropertyId.POSITION_X.id) ||
		it.properties.containsKey(RHMIProperty.PropertyId.POSITION_Y.id)
	}
	val relativeComponents = state.componentsList.filter {
		!it.properties.containsKey(RHMIProperty.PropertyId.POSITION_X.id) &&
		!it.properties.containsKey(RHMIProperty.PropertyId.POSITION_Y.id)
	}
	Box(modifier = modifier
		.padding(10.dp)
		.verticalScroll(rememberScrollState())) {
		Box(modifier = Modifier.sizeIn(10.dp, 10.dp, 1920.dp, 1440.dp)) {
			absoluteComponents.forEach { component ->
				Component(app, component, layout, app.eventHandler, onClickAction)
			}
			Column {
				relativeComponents.forEach { component ->
					Component(app, component, layout, app.eventHandler, onClickAction)
				}
			}
		}
	}
}

internal inline fun Map<Int, RHMIProperty>.applyAsInt(property: RHMIProperty.PropertyId, layout: Int, block: (Int) -> Unit) {
	(this[property.id]?.getForLayout(layout) as? Int)?.apply(block)
}

@Composable
fun Component(app: RHMIAppInfo, component: RHMIComponent, layout: Int,
              eventHandler: (componentId: Int, eventId: Int, args: Map<*, *>) -> Unit, onClickAction: (RHMIAction?, Map<Int, Any>?) -> Unit) {
	val visible = component.properties[RHMIProperty.PropertyId.VISIBLE.id]?.getForLayout(layout)?.asBoolean() != false
	val position = component.properties[RHMIProperty.PropertyId.POSITION_X.id]?.getForLayout(layout) as? Int
	val offScreen = (position ?: 0) > 1950
	if (visible && !offScreen) {
		var modifier: Modifier = Modifier
		component.properties.applyAsInt(RHMIProperty.PropertyId.WIDTH, layout) {
			modifier = modifier.width(it.dp)
		}
		component.properties.applyAsInt(RHMIProperty.PropertyId.HEIGHT, layout) {
			modifier = modifier.height(it.dp)
		}
		component.properties.applyAsInt(RHMIProperty.PropertyId.POSITION_X, layout) {
			modifier = modifier.absoluteOffset(x=it.dp)
		}
		component.properties.applyAsInt(RHMIProperty.PropertyId.POSITION_Y, layout) {
			modifier = modifier.absoluteOffset(y=it.dp)
		}

		when (component) {
			is RHMIComponent.Label -> TextModel(model = component.getModel(), modifier = modifier)
			is RHMIComponent.Button -> TextModel(model = component.getModel(), modifier = modifier.clickable { onClickAction(component.getAction(), null) })
			is RHMIComponent.Separator -> Divider(modifier = modifier)
			is RHMIComponent.Image -> ImageModel(model = component.getModel(), modifier = modifier)
			is RHMIComponent.List -> List(component = component, modifier = modifier, eventHandler = eventHandler, onClickAction = onClickAction)
			is RHMIComponent.Gauge -> Gauge(model = component.getModel(), modifier = modifier)
		}
	}
}

val LocalTextDB = staticCompositionLocalOf { emptyMap<String, Map<Int, String>>() }