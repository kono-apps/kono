package kono.generated

import kono.KonoApplicationContext

val GeneratedAppContext by lazy {
    Class.forName("kono.generated.GeneratedKonoContext")
        .getDeclaredConstructor()
        .newInstance() as KonoApplicationContext
}