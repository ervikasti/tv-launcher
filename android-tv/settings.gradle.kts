// Gradle settings: tells Gradle where to download plugins/libraries and which module to build.
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
    }
}

rootProject.name = "SahajLauncher"
include(":app")
