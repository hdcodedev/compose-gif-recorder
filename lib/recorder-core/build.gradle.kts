plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
    alias(libs.plugins.vanniktech.publish)
}

kotlin {
    jvmToolchain(17)
}

dokka {
    dokkaSourceSets.configureEach {
        documentedVisibilities.set(
            setOf(org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Public),
        )
        skipEmptyPackages.set(true)
        jdkVersion.set(17)
    }
}

dependencies {
    api(libs.compose.runtime)
    testImplementation(kotlin("test"))
    testImplementation(libs.junit4)
}

mavenPublishing {
    coordinates(
        groupId = ProjectConfig.group,
        artifactId = "compose-gif-recorder-core",
        version = ProjectConfig.version,
    )

    pom {
        ProjectPublishing.configurePom(
            pom = this,
            moduleName = "Compose GIF Recorder Core",
            moduleDescription = "Core models and contracts for compose-gif-recorder",
        )
    }
}
