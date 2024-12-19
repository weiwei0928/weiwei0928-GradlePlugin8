pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven(url = "https://jitpack.io")
        maven(url = uri("./repo"))
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://jitpack.io")
        maven(url = uri("./repo"))
        google()
        mavenCentral()
    }
}

rootProject.name = "GradlePlugin8"
include(":app")
include(":hookLib")
includeBuild("gradle-plugin-lib")
include(":hookTest")
