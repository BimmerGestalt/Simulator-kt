package io.bimmergestalt.headunit.models

import io.bimmergestalt.idriveconnectkit.rhmi.RHMIApplication
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel

class ImageModel(val app: RHMIApplication, id: Int): RHMIModel(id) {
	val image: ImageTintable?
		get() = app.getModel(id) as? ImageTintable
}