plugins {
    id("br.dev.pedrolamarao.metal.library")
    id("br.dev.pedrolamarao.metal.cxx")
}

dependencies {
    api(project(":base"))
    implementation(project(":internal"))
}

metal {
    cpp {
        main {
            public = true
        }
    }
}