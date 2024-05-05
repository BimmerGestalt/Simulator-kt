package io.bimmergestalt.headunit.rhmi

import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIApplication
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel

class RHMIModelHeadunit {
	class ImageModel(val app: RHMIApplication, id: Int): RHMIModel(id) {
		val image: ImageTintable?
			get() = app.getModel(id) as? ImageTintable
	}
}