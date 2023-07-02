package kono.app

import kono.generated.GeneratedAppContext
import kono.webview.WebViewBuilder
import java.io.File

fun runKonoApplication(
    context: KonoApplicationContext = GeneratedAppContext,
    block: KonoApplicationBuilder.() -> Unit = {}
) {
    konoApplication(context, block).start()
}

fun konoApplication(
    context: KonoApplicationContext = GeneratedAppContext,
    block: KonoApplicationBuilder.() -> Unit = {}
): KonoApplication {
    val builder = KonoApplicationBuilder(context)
    builder.block()
    return builder.build()
}

fun WebViewBuilder.addScripts() {
    for (file in File("F:\\Java\\kono-apps\\kono\\kono-js").listFiles()!!)
        if (file.extension == "js")
            addInitializationScript(file.readText())
}