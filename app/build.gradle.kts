import com.android.build.gradle.internal.tasks.factory.dependsOn
import java.net.URI

plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
}

android {
	namespace = "io.bimmergestalt.headunit"
	compileSdk = 34

	defaultConfig {
		applicationId = "io.bimmergestalt.headunit"
		minSdk = 24
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
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.1"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
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

dependencies {

	implementation("androidx.core:core-ktx:1.12.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
	implementation("androidx.activity:activity-compose:1.8.2")
	implementation(platform("androidx.compose:compose-bom:2023.08.00"))
	implementation("androidx.compose.foundation:foundation:1.6.2")
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.navigation:navigation-compose:2.7.7")
	implementation("ar.com.hjg:pngj:2.1.0")
	implementation("io.github.reactivecircus.cache4k:cache4k:0.13.0")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")

//	implementation("io.bimmergestalt:IDriveConnectKit:0.6")
	implementation(project(":IDriveConnectKit"))
}