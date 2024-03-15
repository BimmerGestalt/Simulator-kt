import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig


plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.jetbrainsCompose)
}

kotlin {
	androidTarget {
		compilations.all {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
	}

	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		moduleName = "app"
		browser {
			commonWebpackConfig {
				outputFileName = "app.js"
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
		androidMain.dependencies {
			implementation(libs.androidx.activity.compose)
			implementation(libs.compose.foundation.android)
			implementation(libs.compose.material3.android)
			implementation(libs.cache4k)
			implementation(libs.pngj)

			implementation(projects.iDriveConnectKit)
		}

		commonMain.dependencies {
			implementation(compose.runtime)
			implementation(compose.foundation)
			implementation(compose.material3)
			implementation(compose.ui)
			@OptIn(ExperimentalComposeLibrary::class)
			implementation(compose.components.resources)

//			api(libs.moko.compose)
			implementation(libs.kotlinx.datetime)
			implementation(libs.voyager.navigator)
			implementation(libs.voyager.screenModel)
			implementation(libs.voyager.tabNavigator)
			implementation(libs.voyager.transitions)

			implementation(compose.components.uiToolingPreview)
		}
	}
}

android {
	namespace = "io.bimmergestalt.headunit"
	compileSdk = 34

	sourceSets["main"].java.srcDirs("src/androidMain/java")
	sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
	sourceSets["main"].res.srcDirs("src/androidMain/res")

	defaultConfig {
		applicationId = "io.bimmergestalt.headunit"
		minSdk = 26
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.4"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}

	dependencies {
		debugImplementation(libs.compose.ui.tooling)
		debugImplementation(libs.compose.ui.test.manifest)
		testImplementation(libs.junit)
		androidTestImplementation(libs.androidx.test.junit)
		androidTestImplementation(libs.androidx.test.espresso)
		androidTestImplementation(libs.androidx.test.compose)
	}
}

compose.experimental {
	web.application {}
}

tasks.named("preBuild").dependsOn(":IDriveConnectKit:compileEtch")
tasks.named("preBuild").dependsOn(":IDriveConnectKit:extractEtchRuntime")


//tasks["desugarDebugFileDependencies"].dependsOn("extractEtchRuntime")
gradle.taskGraph.whenReady(closureOf<TaskExecutionGraph> {
	println("Found task graph: $this")
	println("Found " + allTasks.size + " tasks.")
	allTasks.forEach { task ->
		println(task)
		task.dependsOn.forEach { dep ->
			println("  - $dep")
		}
	}
})