import java.net.URI

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	repositories {
		google()
		mavenCentral()
		maven { url = URI("https://jitpack.io") }
	}
}

rootProject.name = "Headunit-kt"
include(":app")
include(":IDriveConnectKit")
