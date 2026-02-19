plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
    signing
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
        ProjectPublishing.configurePom(
            pom = this,
            moduleName = "Compose GIF Recorder Annotations",
            moduleDescription = "Annotation API for compose-gif-recorder"
        )
    }
}
