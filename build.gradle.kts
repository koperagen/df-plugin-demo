plugins {
    kotlin("jvm") version "2.0.20"
}

repositories {
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:0.15.0-dev-4593")
    kotlinCompilerPluginClasspath("org.jetbrains.kotlinx.dataframe:compiler-plugin-all:0.15.0-dev-4593")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.7.1")
    implementation("org.jsoup:jsoup:1.18.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

tasks.compileKotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-P", "plugin:org.jetbrains.kotlinx.dataframe:path=${projectDir.absolutePath}")
        freeCompilerArgs.addAll("-P", "plugin:org.jetbrains.kotlinx.dataframe:schemas=${layout.buildDirectory.file("generated").get().asFile.absolutePath}")
    }
    compilerExecutionStrategy.set(org.jetbrains.kotlin.gradle.tasks.KotlinCompilerExecutionStrategy.IN_PROCESS)
}