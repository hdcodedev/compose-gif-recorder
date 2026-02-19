plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.vanniktech.publish)
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

mavenPublishing {
    coordinates(
        groupId = ProjectConfig.group,
        artifactId = "compose-gif-recorder-ksp",
        version = ProjectConfig.version
    )

    pom {
        name.set("Compose GIF Recorder KSP")
        description.set("KSP processor for compose-gif-recorder")
    }
}
