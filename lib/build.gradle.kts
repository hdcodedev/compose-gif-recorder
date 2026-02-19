plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.vanniktech.publish) apply false
}

allprojects {
    group = ProjectConfig.group
    version = ProjectConfig.version

    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    plugins.withId("com.vanniktech.maven.publish") {
        extensions.configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
            pom {
                url.set("https://github.com/hdcodedev/compose-gif-recorder")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("hdcodedev")
                        name.set("hdcodedev")
                        url.set("https://github.com/hdcodedev")
                    }
                }
                scm {
                    url.set("https://github.com/hdcodedev/compose-gif-recorder")
                    connection.set("scm:git:git://github.com/hdcodedev/compose-gif-recorder.git")
                    developerConnection.set("scm:git:ssh://git@github.com/hdcodedev/compose-gif-recorder.git")
                }
            }
        }
    }
}
