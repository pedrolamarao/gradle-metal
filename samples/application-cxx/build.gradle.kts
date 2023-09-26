plugins {
    id("br.dev.pedrolamarao.metal.base2")
    id("br.dev.pedrolamarao.metal.cxx")
}

metal {
    cpp {
        sources {
            create("main")
        }
    }
    cxx {
        sources {
            create("main")
        }
    }
    applications {
        create("main") {
            source( cxx.sources.named("main").map { it.outputs } )
        }
    }
}
