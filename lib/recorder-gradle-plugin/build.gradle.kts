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
            id = "io.github.hdcodedev.compose-gif-recorder"
            implementationClass = "io.github.hdcodedev.composegif.plugin.ComposeGifRecorderPlugin"
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

afterEvaluate {
    publishing {
        publications {
            named<MavenPublication>("pluginMaven") {
                artifactId = "compose-gif-recorder-gradle-plugin"
                pom {
                    name.set("Compose GIF Recorder Gradle Plugin")
                    description.set("Gradle plugin for compose-gif-recorder")
                    url.set("https://github.com/hdcodedev/compose-gif-recorder")
                }
            }
        }
    }
}
