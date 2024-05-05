package io.bimmergestalt.headunit.utils
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.idriveconnectkit.rhmi.RHMIModel

fun loadImage(model: RHMIModel?): ImageTintable? {
	val app = when (model) {
		is RHMIModel.ImageIdModel -> model.app
		is RHMIModel.RaImageModel -> model.app
		else -> null
	}
	val value = app?.getModel(model?.id ?: -1)
	return value as? ImageTintable
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