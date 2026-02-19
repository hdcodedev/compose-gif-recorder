rootProject.name = "compose-gif-recorder-lib"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
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
