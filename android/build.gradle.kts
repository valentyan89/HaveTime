// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ktlint) apply false
}

tasks.register("ktlintFormatter") {
    group = "formatting"
    description = "Formats Kotlin code in all configured modules"
    dependsOn("ktlintFormat")
}

tasks.register("clean", Delete::class) {
    group = "build"
    description = "Cleans the build directory"
    delete(rootProject.buildDir)
}
