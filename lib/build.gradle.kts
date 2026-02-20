import org.gradle.api.tasks.Copy

plugins {
    base
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.vanniktech.publish) apply false
    alias(libs.plugins.ktlint) apply false
}

val publishableModules = listOf(
    ":recorder-annotations",
    ":recorder-core",
    ":recorder-ksp",
    ":recorder-android",
    ":recorder-gradle-plugin"
)

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension>("ktlint") {
        android.set(true)
        ignoreFailures.set(false)
    }
}

allprojects {
    group = ProjectConfig.group
    version = ProjectConfig.version

    repositories {
        google()
        mavenCentral()
    }
}

val testableJvmModules = listOf(
    ":recorder-annotations",
    ":recorder-core",
    ":recorder-ksp",
    ":recorder-gradle-plugin",
)

tasks.register("recorderTest") {
    group = "verification"
    description = "Runs JVM/unit tests for all recorder modules"
    dependsOn(testableJvmModules.map { "$it:test" })
    dependsOn(":recorder-android:testDebugUnitTest")
}

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

tasks.register<Copy>("dokkaPublicApi") {
    group = "documentation"
    description = "Generates Dokka HTML docs for published recorder modules"
    dependsOn(publishableModules.map { "$it:dokkaGeneratePublicationHtml" })
    into(layout.buildDirectory.dir("dokka/public-api"))
    publishableModules.forEach { modulePath ->
        val moduleName = modulePath.removePrefix(":")
        from(project(modulePath).layout.buildDirectory.dir("dokka/html")) {
            into(moduleName)
        }
    }
}
