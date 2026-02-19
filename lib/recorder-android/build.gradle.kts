plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
    signing
    alias(libs.plugins.vanniktech.publish)
}

android {
    namespace = "io.github.hdcodedev.composegif.android"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    api(project(":recorder-core"))

    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.activity.compose)

    api(libs.compose.ui.test.junit4)
    api(libs.compose.ui.test.manifest)
    api(libs.androidx.test.ext.junit)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.junit4)
}

mavenPublishing {
    configure(com.vanniktech.maven.publish.AndroidSingleVariantLibrary(
        javadocJar = com.vanniktech.maven.publish.JavadocJar.None(),
        sourcesJar = com.vanniktech.maven.publish.SourcesJar.Sources(),
        variant = "release",
    ))

    coordinates(
        groupId = ProjectConfig.group,
        artifactId = "compose-gif-recorder-android",
        version = ProjectConfig.version
    )

    pom {
        ProjectPublishing.configurePom(
            pom = this,
            moduleName = "Compose GIF Recorder Android",
            moduleDescription = "Android deterministic frame capture runtime for compose-gif-recorder"
        )
    }
}
