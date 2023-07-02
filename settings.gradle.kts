pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("repos/maven-repo")}
    }
}

rootProject.name = "kono"

include(
    "app",
    "codegen",
    "gradle-plugin",
    "common"
)
