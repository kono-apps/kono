package kono.app

import kono.generated.GeneratedAppContext
import kono.webview.WebViewBuilder
import java.io.File

fun runKonoApplication(
    context: KonoApplicationContext = GeneratedAppContext,
    block: KonoApplicationBuilder.() -> Unit = {},
) {
    konoApplication(context, block).start()
}

fun konoApplication(
    context: KonoApplicationContext = GeneratedAppContext,
    block: KonoApplicationBuilder.() -> Unit = {},
): KonoApplication {
    val builder = KonoApplicationBuilder(context)
    builder.block()
    return builder.build()
}
