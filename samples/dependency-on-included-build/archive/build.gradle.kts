plugins {
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.cxx")
}

group = "br.dev.pedrolamarao.gradle.metal.sample"
version = "1.0"

metal {
    cpp {
        main {
            public = true
        }
    }
}

tasks.wrapper.configure {
    gradleVersion = "8.4"
}