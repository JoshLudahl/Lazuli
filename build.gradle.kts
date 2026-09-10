
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ktlint)
}

tasks.register("clean", Delete::class) {
    description = "clean"
    delete(rootProject.layout.buildDirectory.get())
}
