plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-gradle-plugin")
    `maven-publish`
    signing
    alias(libs.plugins.vanniktech.publish)
}

kotlin {
    jvmToolchain(17)
}

val generateVersionResource by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/version-resource")
    inputs.property("version", ProjectConfig.version)
    outputs.dir(outputDir)
    doLast {
        val file = outputDir.get().file("compose-gif-recorder.version").asFile
        file.parentFile.mkdirs()
        file.writeText(ProjectConfig.version)
    }
}

sourceSets.main {
    resources.srcDir(layout.buildDirectory.dir("generated/version-resource"))
}

tasks.named("processResources") {
    dependsOn(generateVersionResource)
}

tasks.withType<Jar>().configureEach {
    dependsOn(generateVersionResource)
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

mavenPublishing {
    configure(
        com.vanniktech.maven.publish.GradlePlugin(
            javadocJar =
                com.vanniktech.maven.publish.JavadocJar
                    .Empty(),
            sourcesJar =
                com.vanniktech.maven.publish.SourcesJar
                    .Sources(),
        ),
    )

    coordinates(
        groupId = ProjectConfig.group,
        artifactId = "compose-gif-recorder-gradle-plugin",
        version = ProjectConfig.version,
    )

    pom {
        ProjectPublishing.configurePom(
            pom = this,
            moduleName = "Compose GIF Recorder Gradle Plugin",
            moduleDescription = "Gradle plugin for compose-gif-recorder",
        )
    }
}
