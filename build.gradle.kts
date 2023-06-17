plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "revxrsal"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://gitlab.com/api/v4/projects/38224197/packages/maven")
}

dependencies {
    implementation("com.github.winterreisender:webviewko-jvm:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}