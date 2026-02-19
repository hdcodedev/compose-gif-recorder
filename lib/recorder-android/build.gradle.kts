plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
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

publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }
            artifactId = "compose-gif-recorder-android"
            pom {
                name.set("Compose GIF Recorder Android")
                description.set("Android deterministic frame capture runtime for compose-gif-recorder")
                url.set("https://github.com/hdcodedev/compose-gif-recorder")
            }
        }
    }
}
