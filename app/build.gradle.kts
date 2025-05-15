plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    val target = 36
    compileSdk = target

    defaultConfig {
        applicationId = "com.softklass.lazuli"
        minSdk = 26
        targetSdk = target
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    namespace = "com.softklass.lazuli"
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
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.text.google.fonts)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // DATASTORE
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)

    // APPSEARCH
    implementation(libs.androidx.appsearch)
    // Use kapt instead of annotationProcessor if writing Kotlin classes
    annotationProcessor(libs.androidx.appsearch.compiler)

    implementation(libs.androidx.appsearch.local.storage)
    // PlatformStorage is compatible with Android 12+ devices, and offers additional features
    // to LocalStorage.
    implementation(libs.androidx.appsearch.platform.storage)
}
