import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0" apply false
}

group = "kono"
version = "0.1"

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}