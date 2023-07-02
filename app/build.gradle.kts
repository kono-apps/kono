import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.8.0-1.0.9"
}

group = "kono"
version = "0.1"

repositories {
    mavenCentral()
    maven(url = "https://gitlab.com/api/v4/projects/38224197/packages/maven")
    maven(url = "../repos/maven-repo")
}

dependencies {

    // JNA for accessing native APIs
    implementation("net.java.dev.jna:jna:5.13.0")

    // Moshi for handling JSON
    implementation("com.squareup.moshi:moshi:1.14.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
}

kotlin {
    jvmToolchain(11)
}

tasks.withType(KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}