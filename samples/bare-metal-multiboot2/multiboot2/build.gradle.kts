plugins {
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.cxx")
}

metal {
    compileOptions = listOf("-fasm-blocks","-std=c++20")
    cpp {
        main {
            public = true
        }
    }
}