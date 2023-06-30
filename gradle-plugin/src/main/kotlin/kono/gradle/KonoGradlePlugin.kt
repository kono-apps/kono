package kono.gradle

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.google.devtools.ksp.gradle.KspExtension
import com.google.devtools.ksp.gradle.KspGradleSubplugin
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.JavaPluginExtension

interface KonoPluginExtension {
    var mainClass: String
    var config: String?
}

const val KSP_PLUGIN = "com.google.devtools.ksp"
const val SHADOW_PLUGIN = "com.github.johnrengelman.shadow"

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
        if (!project.pluginManager.hasPlugin("application")) {
            project.pluginManager.apply("application")
            println("Adding application plugin")
        }

        project.addKonoDependencies()
        project.addMoshiDependencies()
        project.extensions.getByType(KspExtension::class.java).apply {
            arg("kono:projectDir", project.rootProject.projectDir.absolutePath)
        }

        project.extensions.getByType(JavaPluginExtension::class.java).apply {
            sourceSets.named("main") {
                it.resources.srcDirs(project.rootProject.projectDir.resolve("dist"))
            }
        }
    }
}

fun Project.addKonoDependencies() {
    dependencies.add("implementation", project.project(":runtime"))
    dependencies.add("compileOnly", project.project(":annotations"))
    dependencies.add("ksp", project.project(":codegen"))
}

fun Project.addMoshiDependencies() {
    dependencies.add("implementation", "com.squareup.moshi:moshi:1.14.0")
    dependencies.add("ksp", "com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
}
