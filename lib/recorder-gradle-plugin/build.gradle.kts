plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-gradle-plugin")
    id("maven-publish")
}

kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    plugins {
        create("composeGifRecorder") {
            id = "io.github.dautovicharis.compose-gif-recorder"
            implementationClass = "io.github.dautovicharis.composegif.plugin.ComposeGifRecorderPlugin"
            displayName = "Compose GIF Recorder Plugin"
            description = "Generates deterministic GIFs from annotated Compose scenarios"
        }
    }
}

dependencies {
    implementation(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation(libs.junit4)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "compose-gif-recorder-gradle-plugin"
            pom {
                name.set("Compose GIF Recorder Gradle Plugin")
                description.set("Gradle plugin for compose-gif-recorder")
                url.set("https://github.com/dautovicharis/charts")
            }
        }
    }
}
