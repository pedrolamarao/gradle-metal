plugins {
    id("br.dev.pedrolamarao.metal.base2")
    id("br.dev.pedrolamarao.metal.c")
}

metal {
    cpp {
        create("main")
    }
    c {
        create("main") {
            header( cpp.named("main").map { it.sources.sourceDirectories } )
        }
    }
    applications {
        create("main") {
            source( c.named("main").map { it.outputs } )
        }
    }
}
