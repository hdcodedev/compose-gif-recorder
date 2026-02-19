plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.vanniktech.publish) apply false
}

allprojects {
    group = ProjectConfig.group
    version = ProjectConfig.version

    repositories {
        google()
        mavenCentral()
    }
}

val publishableModules = listOf(
    ":recorder-annotations",
    ":recorder-core",
    ":recorder-ksp",
    ":recorder-android",
    ":recorder-gradle-plugin"
)

tasks.register("publishRecorderModules") {
    group = "publishing"
    description = "Publishes all recorder modules to the configured Maven repository"
    dependsOn(publishableModules.map { "$it:publish" })
}

tasks.register("publishRecorderModulesToMavenLocal") {
    group = "publishing"
    description = "Publishes all recorder modules to Maven Local"
    dependsOn(publishableModules.map { "$it:publishToMavenLocal" })
}
