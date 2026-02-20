pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

includeBuild("lib") {
    dependencySubstitution {
        substitute(module("io.github.hdcodedev:compose-gif-recorder-annotations"))
            .using(project(":recorder-annotations"))
        substitute(module("io.github.hdcodedev:compose-gif-recorder-core"))
            .using(project(":recorder-core"))
        substitute(module("io.github.hdcodedev:compose-gif-recorder-ksp"))
            .using(project(":recorder-ksp"))
        substitute(module("io.github.hdcodedev:compose-gif-recorder-android"))
            .using(project(":recorder-android"))
    }
}

rootProject.name = "demo"
include(":app")
