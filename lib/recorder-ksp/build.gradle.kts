plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

kotlin {
    jvmToolchain(17)
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "compose-gif-recorder-ksp"
            pom {
                name.set("Compose GIF Recorder KSP")
                description.set("KSP processor for compose-gif-recorder")
                url.set("https://github.com/dautovicharis/charts")
            }
        }
    }
}
