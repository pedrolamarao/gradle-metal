plugins {
    id("br.dev.pedrolamarao.metal.library")
    id("br.dev.pedrolamarao.metal.cxx")
}

library {
    compileOptions = listOf("-std=c++20")
}

tasks.wrapper.configure {
    gradleVersion = "8.4"
}