plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "1.1.0"
    `maven-publish`
}

publishing {
    repositories {
        maven {
            url = uri("../repos/maven-repo")
        }
    }
}

group = "kono.app"
version = "0.1"

gradlePlugin {
    website.set("https://github.com/kono-apps/kono")
    vcsUrl.set("https://github.com/kono-apps/kono.git")
    plugins {
        create("kono") {
            id = "kono.app"
            displayName = "Kono Apps"
            implementationClass = "kono.gradle.KonoGradlePlugin"
            description = "A Gradle plugin for setting up a Kono workspace"
            tags.set(listOf("kono", "kono-apps", "desktop", "cross-platform"))
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.8.0-1.0.9")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
}

kotlin {
    jvmToolchain(11)
}