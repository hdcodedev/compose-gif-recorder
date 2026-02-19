plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "compose-gif-recorder-annotations"
            pom {
                name.set("Compose GIF Recorder Annotations")
                description.set("Annotation API for compose-gif-recorder")
                url.set("https://github.com/hdcodedev/compose-gif-recorder")
            }
        }
    }
}
