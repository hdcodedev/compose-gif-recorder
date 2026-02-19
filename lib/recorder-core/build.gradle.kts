plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(libs.compose.runtime)
    testImplementation(kotlin("test"))
    testImplementation(libs.junit4)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "compose-gif-recorder-core"
            pom {
                name.set("Compose GIF Recorder Core")
                description.set("Core models and contracts for compose-gif-recorder")
                url.set("https://github.com/dautovicharis/charts")
            }
        }
    }
}
