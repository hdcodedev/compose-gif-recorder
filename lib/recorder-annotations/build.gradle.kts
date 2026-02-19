plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.vanniktech.publish)
}

kotlin {
    jvmToolchain(17)
}

mavenPublishing {
    coordinates(
        groupId = ProjectConfig.group,
        artifactId = "compose-gif-recorder-annotations",
        version = ProjectConfig.version
    )

    pom {
        name.set("Compose GIF Recorder Annotations")
        description.set("Annotation API for compose-gif-recorder")
    }
}
