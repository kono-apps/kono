pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("repos/maven-repo")}
    }
}

rootProject.name = "kono"

include(
    "annotations",
    "app",
    "codegen",
    "runtime",
    "gradle-plugin"
)
