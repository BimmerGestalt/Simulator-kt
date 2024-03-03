package io.bimmergestalt.headunit.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/// https://euryperez.dev/consuming-flows-safely-in-non-composable-scopes-in-jetpack-compose-d0154565bd68

/**
 * Remembers the result of [flowWithLifecycle]. Updates the value if the [flow]
 * or [lifecycleOwner] changes. Cancels collection in onStop() and start it in onStart()
 *
 * @param flow The [Flow] that is going to be collected.
 * @param lifecycleOwner The [LifecycleOwner] to validate the [Lifecycle.State] from
 *
 * @return [Flow] with the remembered value of type [T]
 */
@Composable
fun <T> rememberFlowWithLifecycle( // 1
	flow: Flow<T>,
	lifecycleOwner: LifecycleOwner
): Flow<T> {
	return remember(flow, lifecycleOwner) { // 2
		flow.flowWithLifecycle( // 3
			lifecycleOwner.lifecycle, // 4
			Lifecycle.State.STARTED // 5
		)
	}
}

/**
 * Adds a LaunchedEffect to the composition, that will relaunch only if the flow changes.
 * This function uses flowWithLifecycle() to collect the flows in a lifecycle-aware manner.
 *
 * @param flow The [Flow] that is going to be collected.
 * @param onEffectConsumed Callback to mark the event as consumed
 * @param function The block that will get executed on flow collection
 */
@Composable
fun <T> LaunchedEffectAndCollect(
	flow: Flow<T?>, // 1
	function: suspend (value: T) -> Unit // 2
) {
	val effectFlow =
		rememberFlowWithLifecycle(flow, LocalLifecycleOwner.current) // 3

	LaunchedEffect(effectFlow) { // 4
		effectFlow.mapNotNull { it }.collect(function) // 5
	}
}