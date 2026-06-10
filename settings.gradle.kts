pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
        mavenCentral()
        gradlePluginPortal()
    }
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlinx:dataframe-core:0.14.1")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "df-plugin-demo"