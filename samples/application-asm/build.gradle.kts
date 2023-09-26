plugins {
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.base2")
}

metal {
    asm {
        create("main")
    }
    applications {
        create("main") {
            source( asm.named("main").map { it.outputs } )
        }
    }
}
