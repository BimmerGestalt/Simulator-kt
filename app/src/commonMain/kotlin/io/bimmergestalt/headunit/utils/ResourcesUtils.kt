package io.bimmergestalt.headunit.utils
import io.bimmergestalt.headunit.models.ImageModel
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel

fun loadImage(model: RHMIModel?): ImageTintable? {
	if (model is ImageModel) {
		return model.image
	}
	return null
}

fun loadText(model: RHMIModel?, textDB: Map<String, Map<Int, String>>): String {
	return when (model) {
		is RHMIModel.TextIdModel -> {
			val dictionary = textDB["en-US"] ?: emptyMap()    // TODO use context locale
			val textId = model.textId
			dictionary[textId] ?: ""
		}

		is RHMIModel.RaDataModel -> {
			model.value as? String ?: ""
		}

		else -> ""
	}
}