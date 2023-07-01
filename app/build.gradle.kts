plugins {
    id("kono.app") version "0.1"
    kotlin("jvm")
//    id("com.google.devtools.ksp") version "1.8.0-1.0.9"
//    id("com.github.johnrengelman.shadow") version "7.0.0"
//    application
}

group = "kono"
version = "0.1"

kono {
    mainClass = "kono.MainKt"
}

repositories {
    mavenCentral()
    maven(url = "https://gitlab.com/api/v4/projects/38224197/packages/maven")
    maven(url = "../repos/maven-repo")
}

dependencies {
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
//
//    // Moshi for handling JSON
//    implementation("com.squareup.moshi:moshi:1.14.0")
//    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
//
//    implementation(project(":runtime"))
//    compileOnly(project(":common"))
//    ksp(project(":codegen"))
}

application {
    mainClass.set("kono.MainKt")
}

kotlin {
    jvmToolchain(11)
}

//sourceSets {
//    getByName("main") {
//        resources.srcDirs(rootProject.projectDir.resolve("dist"))
//    }
//}

// Makes generated code visible to IDE
kotlin {
    sourceSets.main {
        kotlin.srcDirs(file("$buildDir/generated/ksp/main/kotlin"))
    }
    sourceSets.test {
        kotlin.srcDir("$buildDir/generated/ksp/test/kotlin")
    }
}

