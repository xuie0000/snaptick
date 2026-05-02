plugins {
	id("com.android.library")
	id("org.jetbrains.kotlin.android")
}

android {
	namespace = "com.vishal2376.snaptick.wheelpicker"
	compileSdk = 34

	defaultConfig {
		minSdk = 26
		consumerProguardFiles("consumer-rules.pro")
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
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
		freeCompilerArgs += listOf(
			"-Xopt-in=dev.chrisbanes.snapper.ExperimentalSnapperApi"
		)
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.11"
	}
}

dependencies {
	implementation(platform("androidx.compose:compose-bom:2023.08.00"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.foundation:foundation")
	implementation("androidx.compose.material3:material3")
	implementation("dev.chrisbanes.snapper:snapper:0.3.0")
}
