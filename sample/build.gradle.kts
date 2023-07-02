plugins {
    kotlin("jvm")
    id("kono.app") version "0.1"
}

kono {
    assetsDir = "../dist"
    mainClass = "kono.samples.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app"))
    ksp(project(":codegen"))
}
