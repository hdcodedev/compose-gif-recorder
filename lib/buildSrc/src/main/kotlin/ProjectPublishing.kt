import org.gradle.api.publish.maven.MavenPom

object ProjectPublishing {
    fun configurePom(
        pom: MavenPom,
        moduleName: String,
        moduleDescription: String
    ) {
        pom.name.set(moduleName)
        pom.description.set(moduleDescription)
        pom.url.set(ProjectConfig.projectUrl)

        pom.licenses {
            license {
                name.set(ProjectConfig.licenseName)
                url.set(ProjectConfig.licenseUrl)
            }
        }
        pom.developers {
            developer {
                id.set(ProjectConfig.developerId)
                name.set(ProjectConfig.developerName)
                url.set(ProjectConfig.developerUrl)
            }
        }
        pom.issueManagement {
            system.set(ProjectConfig.issueSystem)
            url.set(ProjectConfig.issueUrl)
        }
        pom.scm {
            url.set(ProjectConfig.projectUrl)
            connection.set(ProjectConfig.scmConnection)
            developerConnection.set(ProjectConfig.scmDeveloperConnection)
        }
    }
}
