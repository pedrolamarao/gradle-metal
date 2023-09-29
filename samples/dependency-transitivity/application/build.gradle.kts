plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.cxx")
}

dependencies {
    metalImplementation(project(":intermediate"))
}