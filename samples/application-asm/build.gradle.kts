plugins {
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.base2")
}

metal {
    asm {
        sources {
            create("main")
        }
    }
    applications {
        create("main") {
            source( asm.sources.named("main").map { it.outputs } )
        }
    }
}
