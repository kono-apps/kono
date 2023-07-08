import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.8.0-1.0.9"
    signing
    `maven-publish`
}

group = "kono"
version = "0.1"

java {
    withJavadocJar()
    withSourcesJar()
}

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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.company"
            artifactId = "sample"
            version = "0.9-SNAPSHOT"
            from(components["java"])
            pom {
                name.set("Kono API")
                description.set("A cross-platform framework for building native desktop apps using the web for frontend")
                url.set("https://github.com/kono-apps/kono")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/license/mit/")
                    }
                }
                developers {
                    developer {
                        id.set("revxrsal")
                        name.set("Revxrsal")
                        email.set("reflxction.github@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/kono-apps/kono.git")
                    developerConnection.set("scm:git:ssh://github.com/kono-apps/kono.git")
                    url.set("https://github.com/kono-apps/kono.git")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}
