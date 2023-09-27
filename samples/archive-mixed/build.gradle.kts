plugins {
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.c")
    id("br.dev.pedrolamarao.metal.cxx")
}

tasks.wrapper.configure {
    gradleVersion = "8.3"
}