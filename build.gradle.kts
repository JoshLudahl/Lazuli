

plugins {
    id("com.android.application") version "8.4.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    alias(libs.plugins.compose.compiler) apply false

}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory.get())
}
