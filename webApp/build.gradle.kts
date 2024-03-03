import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

/// Copied from https://github.com/Kotlin/kotlin-wasm-examples/blob/main/compose-example/composeApp/build.gradle.kts
plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.jetbrainsCompose)
}

kotlin {
	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		moduleName = "webApp"
		browser {
			commonWebpackConfig {
				outputFileName = "headunit.js"
				devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
					static = (static ?: mutableListOf()).apply {
						// Serve sources to debug inside browser
						add(project.projectDir.path)
						add(project.projectDir.path + "/commonMain/")
						add(project.projectDir.path + "/wasmJsMain/")
					}
				}
			}
		}
		binaries.executable()
	}

	sourceSets {
		commonMain.dependencies {
//			implementation(project(":app"))
			implementation(libs.compose.ui)
			implementation(libs.compose.foundation)
//			implementation(libs.compose.material3)
		}
	}
}

compose.experimental {
	web.application {}
}