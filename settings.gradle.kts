pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie" }
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
    id("dev.kikugie.loom-back-compat") version "0.4.2"
}

stonecutter {
    create(rootProject) {
        versions("1.21.4", "26.2", "26.3")
        vcsVersion = "26.2"
    }
}

rootProject.name = "litematica-printer"
