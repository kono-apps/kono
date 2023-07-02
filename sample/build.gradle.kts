import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("kono.app") version "0.1"
}

kono {
    assetsDir = "../dist"
    mainClass = "kono.sample.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app"))
    ksp(project(":codegen"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}