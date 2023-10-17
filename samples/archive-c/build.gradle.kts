plugins {
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.c")
    id("br.dev.pedrolamarao.metal.cpp")
}

tasks.wrapper.configure {
    gradleVersion = "8.4"
}