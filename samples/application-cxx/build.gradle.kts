plugins {
    id("br.dev.pedrolamarao.metal.base2")
    id("br.dev.pedrolamarao.metal.cxx")
}

metal {
    cpp {
        create("main")
    }
    cxx {
        create("main") {
            header( cpp.named("main").map { it.sources.sourceDirectories } )
        }
    }
    applications {
        create("main") {
            source( cxx.named("main").map { it.outputs } )
        }
    }
}
