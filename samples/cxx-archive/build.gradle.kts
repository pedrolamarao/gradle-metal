plugins {
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.cxx")
    id("br.dev.pedrolamarao.metal.ixx")
}

metal {
    compileOptions = listOf("-std=c++20")
}

tasks.wrapper.configure {
    gradleVersion = "8.4"
}