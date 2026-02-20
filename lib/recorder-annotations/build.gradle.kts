plugins {
    alias(libs.plugins.kotlin.jvm)
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

mavenPublishing {
    coordinates(
        groupId = ProjectConfig.group,
        artifactId = "compose-gif-recorder-annotations",
        version = ProjectConfig.version,
    )

    pom {
        ProjectPublishing.configurePom(
            pom = this,
            moduleName = "Compose GIF Recorder Annotations",
            moduleDescription = "Annotation API for compose-gif-recorder",
        )
    }
}
