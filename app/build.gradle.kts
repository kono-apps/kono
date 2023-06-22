plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    application
}

group = "kono"
version = "0.1"

repositories {
    mavenCentral()
    maven(url = "https://gitlab.com/api/v4/projects/38224197/packages/maven")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    compileOnly(project(":annotations"))
    implementation(project(":runtime"))
    ksp(project(":codegen"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

// Makes generated code visible to IDE
kotlin {
    sourceSets.main {
        kotlin.srcDirs(file("$buildDir/generated/ksp/main/kotlin"),)
    }
    sourceSets.test {
        kotlin.srcDir("$buildDir/generated/ksp/test/kotlin")
    }
}