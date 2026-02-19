rootProject.name = "compose-gif-recorder-lib"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.vanniktech.maven.publish") version "0.30.0"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

include(":recorder-annotations")
include(":recorder-core")
include(":recorder-ksp")
include(":recorder-android")
include(":recorder-gradle-plugin")
