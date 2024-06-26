package io.bimmergestalt.headunit.rhmi

import android.util.Log
import de.bmw.idrive.BMWRemoting
import io.bimmergestalt.headunit.models.ImageTintable
import io.bimmergestalt.headunit.utils.decodeAndCacheImage
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

data class RHMIResources (
	val app: RHMIApplicationState,
	val textDB: Map<String, Map<Int, String>>,
	val imageDB: Map<Int, ImageTintable>

) {
	companion object {

		fun loadResources(incoming: Map<BMWRemoting.RHMIResourceType, List<ByteArray>>): RHMIResources {
			val app = RHMIApplicationState()
			app.loadFromXML(incoming[BMWRemoting.RHMIResourceType.DESCRIPTION]!!.first())

			val textFiles = incoming.getOrDefault(BMWRemoting.RHMIResourceType.TEXTDB, emptyList()).map {
				loadZipfile(it)
			}.fold(HashMap<String, ByteArray>()) { acc, cur ->
				acc.putAll(cur); acc
			}.mapKeys { it.key.split('.').first()
			}.mapValues {
				loadTextDb(it.value.decodeToString())
			}
			Log.i("Resources", "Loaded textDb $textFiles")

			@Suppress("UNCHECKED_CAST")
			val iconFiles = incoming.getOrDefault(BMWRemoting.RHMIResourceType.IMAGEDB, emptyList()).map {
				loadZipfile(it)
			}.fold(HashMap<String, ByteArray>()) { acc, cur ->
				acc.putAll(cur); acc
			}.mapKeys { it.key.split('.').first().toInt() }.mapValues {
				runBlocking { it.value.decodeAndCacheImage(true) }
			}.filterValues {
				it != null
			} as Map<Int, ImageTintable>

			// convert any hardcoded ImageIds to their ImageBitmaps
			app.modelStates.keys.forEach {
				val value = app.modelStates[it]
				if (value is BMWRemoting.RHMIResourceIdentifier && value.type == BMWRemoting.RHMIResourceType.IMAGEID) {
					app.modelStates[it] = iconFiles[value.id]
				}
			}

			return RHMIResources(app, textFiles, iconFiles)
		}

		fun loadZipfile(input: ByteArray): Map<String, ByteArray> {
			val output = HashMap<String, ByteArray>()
			// based on https://stackoverflow.com/a/66683493/169035
			val zip = ZipInputStream(ByteArrayInputStream(input))
			zip.use {
				generateSequence { it.nextEntry }.forEach {
					output[it.name] = zip.readBytes()
				}
			}
			return output
		}

		fun loadTextDb(input: String): Map<Int, String> {
			return input.lines()
				.map { it.split('=', limit = 2) }
				.filter { it.size == 2 && it[0].toIntOrNull() != null }
				.associate { it[0].toInt() to it[1] }
		}
	}
}
