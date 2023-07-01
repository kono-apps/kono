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
    "runtime",
    "gradle-plugin",
    "common"
)
