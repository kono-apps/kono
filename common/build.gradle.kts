plugins {
    kotlin("jvm")
}

group = "kono"
version = "0.1"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}