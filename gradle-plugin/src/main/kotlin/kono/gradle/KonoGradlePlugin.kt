package kono.gradle

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.google.devtools.ksp.gradle.KspExtension
import com.google.devtools.ksp.gradle.KspGradleSubplugin
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin

interface KonoPluginExtension {
    var mainClass: String?
    var assetsDir: String?
}

const val KSP_PLUGIN = "com.google.devtools.ksp"
const val SHADOW_PLUGIN = "com.github.johnrengelman.shadow"
const val KOTLINX_SERIALIZATION_PLUGIN = "org.jetbrains.kotlin.plugin.serialization"

val Project.kono: KonoPluginExtension get() = extensions.getByName("kono") as KonoPluginExtension

fun Project.kono(configure: Action<KonoPluginExtension>) {
    project.extensions.configure("kono", configure)
}

class KonoGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("kono", KonoPluginExtension::class.java)
        if (!project.pluginManager.hasPlugin(KSP_PLUGIN)) {
            project.pluginManager.apply(KspGradleSubplugin::class.java)
            println("Adding KSP plugin")
        }
        if (!project.pluginManager.hasPlugin(SHADOW_PLUGIN)) {
            project.pluginManager.apply(ShadowPlugin::class.java)
            println("Adding shadow plugin")
        }

        if (!project.pluginManager.hasPlugin(KOTLINX_SERIALIZATION_PLUGIN)) {
            project.pluginManager.apply(SerializationGradleSubplugin::class.java)
            println("Adding kotlinx.serialization plugin")
        }

        if (!project.pluginManager.hasPlugin("application")) {
            project.pluginManager.apply("application")
            println("Adding application plugin")
        }

        project.addKonoDependencies()
        project.addKotlinXSerialization()

        project.afterEvaluate {

            project.extensions.getByType(KspExtension::class.java).apply {
                val assetsDir = project.kono.assetsDir ?: error("Missing property 'assetsDir'")
                arg("kono:projectDir", project.rootProject.projectDir.absolutePath)
                arg("kono:assetsDir", project.file(assetsDir).absolutePath)
            }

            project.extensions.getByType(JavaPluginExtension::class.java).apply {
                val assetsDir = project.kono.assetsDir ?: error("Missing property 'assetsDir'")
                sourceSets.named("main") {
                    it.resources.srcDirs(project.file(assetsDir).absolutePath)
                }
            }

            project.extensions.getByType(JavaApplication::class.java).apply {
                val mainClass = project.kono.mainClass ?: error("Missing property 'mainClass'")
                this.mainClass.set(mainClass)
            }
        }
    }
}

fun Project.addKonoDependencies() {
    dependencies.add("implementation", project.project(":app"))
    dependencies.add("ksp", project.project(":codegen"))
}

fun Project.addKotlinXSerialization() {
    dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}
