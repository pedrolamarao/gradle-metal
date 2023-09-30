plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.cxx")
}

dependencies {
    implementation(project(":archive"))
}

metal {
    cxx {
        named("main") {
            compileOptions = listOf("-std=c++20")
        }
    }
}