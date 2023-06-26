import kono.gradle.KonoPluginExtensions

plugins {
    kotlin("jvm")
//    id("com.google.devtools.ksp")
//    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("kono.app") version "0.1"
//    application
}

group = "kono"
version = "0.1"

kono {
}

repositories {
    mavenCentral()
    maven(url = "https://gitlab.com/api/v4/projects/38224197/packages/maven")
    maven(url = "../repos/maven-repo")
}

dependencies {
//     Kotlin coroutines
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
//
//    // Moshi for handling JSON
//    implementation("com.squareup.moshi:moshi:1.14.0")
//    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("kono.MainKt")
}

// Makes generated code visible to IDE
kotlin {
    sourceSets.main {
        kotlin.srcDirs(file("$buildDir/generated/ksp/main/kotlin"))
    }
    sourceSets.test {
        kotlin.srcDir("$buildDir/generated/ksp/test/kotlin")
    }
}

