plugins {
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.cxx")
}

dependencies {
    implementation(project(":internal"))
}

metal {
    cpp {
        main {
            public = true
        }
    }
}