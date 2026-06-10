plugins {
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.dataframe") version "2.4.0"
}

repositories {
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:1.0.0-Beta5")
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
