plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.cxx")
}

tasks.wrapper.configure {
    gradleVersion = "8.3"
}