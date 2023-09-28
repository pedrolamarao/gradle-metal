plugins {
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.cxx")
    id("br.dev.pedrolamarao.metal.ixx")
}

metal {
    ixx {
        named("main") {
            compileOptions = listOf("-std=c++20")
        }
    }
    cxx {
        named("main") {
            compileOptions = listOf("-std=c++20")
        }
    }
}