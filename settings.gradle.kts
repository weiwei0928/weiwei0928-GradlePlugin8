pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenLocal()
        maven(url = "https://jitpack.io")
//        maven(url = uri("./repo"))
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven(url = "https://jitpack.io")
//        maven(url = uri("./repo"))
        google()
        mavenCentral()
    }
}

rootProject.name = "GradlePlugin"
include(":app")
include(":hookLib")
//includeBuild("gradle-plugin-lib")
include(":gradle-plugin-lib")
include(":hookTest")
