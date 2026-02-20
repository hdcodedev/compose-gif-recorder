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

dependencies {
    implementation(project(":recorder-annotations"))
    implementation(project(":recorder-core"))
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit4)
}

mavenPublishing {
    coordinates(
        groupId = ProjectConfig.group,
        artifactId = "compose-gif-recorder-ksp",
        version = ProjectConfig.version,
    )

    pom {
        ProjectPublishing.configurePom(
            pom = this,
            moduleName = "Compose GIF Recorder KSP",
            moduleDescription = "KSP processor for compose-gif-recorder",
        )
    }
}
