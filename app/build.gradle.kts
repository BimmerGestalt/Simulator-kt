import com.android.build.gradle.internal.tasks.factory.dependsOn
import java.net.URI

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidLibrary)
}

kotlin {
	androidTarget {
		compilations.all {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
	}

	sourceSets {
		commonMain.dependencies {
			//put your multiplatform dependencies here

			implementation(libs.androidx.navigation.runtime.ktx)
			implementation(libs.compose.ui)
			implementation(libs.compose.ui.tooling.preview)
			implementation(libs.compose.foundation)
			implementation(libs.compose.material3)
			implementation(libs.kotlinx.datetime)
			implementation(libs.cache4k)
			implementation(libs.pngj)
		}
		commonTest.dependencies {
			implementation(libs.kotlin.test)
		}
	}
}

android {
	namespace = "io.bimmergestalt.headunit"
	compileSdk = 34

	defaultConfig {
		minSdk = 26
	}
	buildFeatures { // Enables Jetpack Compose for this module
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.4"
	}
}
dependencies {
}
