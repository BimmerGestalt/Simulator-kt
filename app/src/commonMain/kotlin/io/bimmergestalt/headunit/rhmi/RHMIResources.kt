package io.bimmergestalt.headunit.rhmi

import io.bimmergestalt.headunit.models.ImageTintable

data class RHMIResources (
	val app: RHMIApplicationState,
	val textDB: Map<String, Map<Int, String>>,
	val imageDB: Map<Int, ImageTintable>
) {
	companion object {}
}