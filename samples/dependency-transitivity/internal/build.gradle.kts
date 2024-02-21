plugins {
    id("br.dev.pedrolamarao.metal.library")
    id("br.dev.pedrolamarao.metal.cxx")
}

metal {
    cpp {
        main {
            public = true
        }
    }
}