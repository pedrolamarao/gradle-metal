import br.dev.pedrolamarao.gradle.metal.*

plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.cxx")
}

tasks.withType<MetalCompile>().configureEach {
    options = listOf("-std=c++20")
}