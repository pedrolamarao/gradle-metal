plugins {
    id("br.dev.pedrolamarao.metal.library")
    id("br.dev.pedrolamarao.metal.c")
}

tasks.wrapper.configure {
    gradleVersion = "8.4"
}