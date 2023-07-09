package kono.app

import kono.generated.GeneratedAppContext

/**
 * Builds a Kono application and runs it immediately
 */
fun runKonoApplication(
    context: KonoApplicationContext = GeneratedAppContext,
    block: KonoApplicationBuilder.() -> Unit = {},
) {
    konoApplication(context, block).start()
}

/**
 * Builds a Kono application (but does not run it)
 */
fun konoApplication(
    context: KonoApplicationContext = GeneratedAppContext,
    block: KonoApplicationBuilder.() -> Unit = {},
): KonoApplication {
    val builder = KonoApplicationBuilder(context)
    builder.block()
    return builder.build()
}
