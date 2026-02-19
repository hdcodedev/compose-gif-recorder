plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktech.publish)
}

kotlin {
    jvmToolchain(17)
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
        version = ProjectConfig.version
    )

    pom {
        name.set("Compose GIF Recorder Core")
        description.set("Core models and contracts for compose-gif-recorder")
    }
}
