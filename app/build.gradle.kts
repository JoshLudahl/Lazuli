import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serializable)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ktlint)
}

android {

    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.softklass.lazuli"

        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()

        targetSdk =
            libs.versions.targetSdk
                .get()
                .toInt()

        versionCode =
            libs.versions.versionCode
                .get()
                .toInt()

        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments.putAll(mutableMapOf("clearPackageData" to "true"))
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue(type = "string", name = "app_name", value = "Lazuli debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        animationsDisabled = true
    }

    buildFeatures {
        compose = true
    }

    namespace = "com.softklass.lazuli"
}

kotlin {
    kotlin {
        jvmToolchain(21)
    }
}

ktlint {
    android = true
    ignoreFailures = false
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.JSON)
        reporter(ReporterType.HTML)
    }
    additionalEditorconfig.set(
        mapOf(
            "max_line_length" to "off",
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
        ),
    )
}

tasks.named("preBuild") {
    dependsOn("ktlintFormat")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.foundation)

    // MATERIAL 3
    implementation(libs.androidx.material3.android)
    implementation(libs.androidxMaterial3WindowSizeClassAndroid)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // ROOM
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // HILT
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)
    // build.gradle.kts
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    // In App Updates
    implementation(libs.app.update)
    // For Kotlin users also add the Kotlin extensions library for Play In-App Update:
    implementation(libs.app.update.ktx)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // ML Kit Text Recognition
    implementation(libs.mlkit.text.recognition)
    // ML Kit Digital Ink Recognition (for handwriting to text)
    implementation(libs.mlkit.digital.ink)

    // Splash
    implementation(libs.androidx.core.splashscreen)

    // Markdown
    implementation(libs.markdown)
    implementation(libs.richtext.commonmark)

    // Coroutines for Play Services
    implementation(libs.kotlinx.coroutines.play.services)

    // DataStore (preferences)
    implementation(libs.androidx.datastore.preferences)

    // Lottie for Compose
    implementation(libs.lottie.compose)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    androidTestUtil(libs.androidx.orchestrator)
}
