plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.cxx")
}

application {
    compileOptions = listOf("-std=c++20")
}

dependencies {
    implementation(project(":archive"))
}