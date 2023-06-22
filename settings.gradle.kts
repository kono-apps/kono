pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "kono"

include(
    "annotations",
    "app",
    "codegen",
    "runtime"
)
