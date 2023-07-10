import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("kono.app") version "0.1"
}

kono {
    assetsDir = "../../kono-svelte-sample/build"
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

// Optional: Add generated items to source
kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}