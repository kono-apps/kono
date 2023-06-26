plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
}

publishing {
    repositories {
        maven {
            url = uri("../repos/maven-repo")
        }
    }
}

group = "kono"
version = "0.1"

gradlePlugin {
    plugins {
        create("kono") {
            id = "kono.app"
            implementationClass = "kono.gradle.KonoGradlePlugin"
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.8.0-1.0.9")
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
}

kotlin {
    jvmToolchain(11)
}