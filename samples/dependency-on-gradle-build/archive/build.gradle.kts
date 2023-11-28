plugins {
    id("br.dev.pedrolamarao.metal.library")
    id("br.dev.pedrolamarao.metal.cxx")
}

group = "br.dev.pedrolamarao.gradle.metal.sample"
version = "1.0"

tasks.wrapper.configure {
    gradleVersion = "8.4"
}