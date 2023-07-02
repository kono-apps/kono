package kono.generated

import kono.app.KonoApplicationContext

val GeneratedAppContext by lazy {
    Class.forName("kono.generated.GeneratedKonoContext")
        .getDeclaredConstructor()
        .newInstance() as KonoApplicationContext
}