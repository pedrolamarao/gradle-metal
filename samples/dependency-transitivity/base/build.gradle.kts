plugins {
    id("br.dev.pedrolamarao.metal.library")
    id("br.dev.pedrolamarao.metal.cxx")
}

dependencies {
    implementation(project(":internal"))
}