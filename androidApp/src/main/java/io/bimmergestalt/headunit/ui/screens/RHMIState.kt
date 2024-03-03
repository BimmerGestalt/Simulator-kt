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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.headunit.models.RHMIAppInfo
import io.bimmergestalt.headunit.ui.components.Gauge
import io.bimmergestalt.headunit.ui.components.ImageModel
import io.bimmergestalt.headunit.ui.components.List
import io.bimmergestalt.headunit.ui.components.TextModel
import io.bimmergestalt.headunit.ui.components.ToolbarDrawerSheet
import io.bimmergestalt.headunit.ui.controllers.onClickAction
import io.bimmergestalt.headunit.utils.asBoolean
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIAction
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIComponent
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIProperty
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIState
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.MonthNames

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RHMIState(navController: NavController, app: io.bimmergestalt.headunit.models.RHMIAppInfo, stateId: Int) {
	val state = app.resources.app.states[stateId]
	if (state == null) {
		navController.popBackStack()
		return
	}

	val scope = rememberCoroutineScope()
	val onClickAction =
		onClickAction(navController, app)

	DisposableEffect(LocalLifecycleOwner.current) {
		app.eventHandler(stateId, 1, mapOf(4 to true))  // focus
		app.eventHandler(stateId, 11, mapOf(23 to true)) // visible

		onDispose {
			app.eventHandler(stateId, 1, mapOf(4 to false))  // focus
			app.eventHandler(stateId, 11, mapOf(23 to false)) // visible
		}
	}

	CompositionLocalProvider(
		LocalImageDB provides app.resources.imageDB,
		LocalTextDB provides app.resources.textDB
	) {

		Scaffold(
			topBar = {
				val titleText = if (state is RHMIState.CalendarMonthState) {
					val dateInt = state.getDateModel()?.asRaIntModel()?.value ?: 0
					dateInt.fromRhmiDate()
					LocalDate.Format {
						monthName(MonthNames.ENGLISH_FULL)
						chars(", ")
						year()
					}.format(dateInt.fromRhmiDate())
				} else {
					state.getTextModel()?.asRaDataModel()?.value ?: "null"
				}
				TopAppBar(
					title = { Text(titleText) },
					navigationIcon = { IconButton(onClick = {
						navController.popBackStack()
					}){
						Icon(Icons.Filled.ArrowBack, contentDescription=null)
					} }
				)
			}
		) { padding ->
			if (state is RHMIState.ToolbarState) {

				val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
				BackHandler(enabled = drawerState.isOpen) {
					scope.launch { drawerState.close() }
				}
				ModalNavigationDrawer(
					drawerState = drawerState,
					drawerContent = {
						ToolbarDrawerSheet(
							state = state,
							drawerState = drawerState,
							onClickAction = { scope.launch { onClickAction(it, null) } }
						)
					}
				) {
					RHMIStateBody(Modifier.padding(padding), app = app, state = state) { action, args -> scope.launch {
						onClickAction(action, args)
					} }
				}
			} else {
				if (state is RHMIState.CalendarMonthState) {
					RHMICalendarMonthState(
						modifier = Modifier.padding(padding),
						state = state.viewModel(onClickAction)
					)
				} else if (state is RHMIState.CalendarState && state.componentsList.size == 1 && state.componentsList[0] is RHMIComponent.CalendarDay) {
					val calendarState = (state.componentsList[0] as RHMIComponent.CalendarDay).viewModel(onClickAction)
					RHMICalendarState(modifier = Modifier.padding(padding), state = calendarState)
				} else if (state.componentsList.size == 1 && state.componentsList[0] is RHMIComponent.Input) {
					val actionHandler = onClickAction(navController, app, forceAwait = true)
					val inputState = (state.componentsList[0] as RHMIComponent.Input).viewModel(actionHandler)
					RHMIInputState(Modifier.padding(padding), inputState)
				} else {
					RHMIStateBody(Modifier.padding(padding), app = app, state = state) { action, args -> scope.launch {
						onClickAction(action, args)
					} }
				}
			}
		}
	}
}

@Composable
fun RHMIStateBody(modifier: Modifier, app: io.bimmergestalt.headunit.models.RHMIAppInfo, state: RHMIState, onClickAction: (RHMIAction?, Map<Int, Any>?) -> Unit) {
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
	Box(modifier = modifier.verticalScroll(rememberScrollState())) {
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
fun Component(app: io.bimmergestalt.headunit.models.RHMIAppInfo, component: RHMIComponent, layout: Int,
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
			is RHMIComponent.Label -> io.bimmergestalt.headunit.ui.components.TextModel(
				model = component.getModel(),
				modifier = modifier
			)
			is RHMIComponent.Button -> io.bimmergestalt.headunit.ui.components.TextModel(
				model = component.getModel(),
				modifier = modifier.clickable { onClickAction(component.getAction(), null) })
			is RHMIComponent.Separator -> Divider(modifier = modifier)
			is RHMIComponent.Image -> io.bimmergestalt.headunit.ui.components.ImageModel(
				model = component.getModel(),
				modifier = modifier
			)
			is RHMIComponent.List -> io.bimmergestalt.headunit.ui.components.List(
				component = component,
				modifier = modifier,
				eventHandler = eventHandler,
				onClickAction = onClickAction
			)
			is RHMIComponent.Gauge -> io.bimmergestalt.headunit.ui.components.Gauge(
				model = component.getModel(),
				modifier = modifier
			)
		}
	}
}

val LocalImageDB = staticCompositionLocalOf { emptyMap<Int, ImageTintable>() }
val LocalTextDB = staticCompositionLocalOf { emptyMap<String, Map<Int, String>>() }