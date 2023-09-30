plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.cxx")
}

group = "br.dev.pedrolamarao.gradle.metal.sample"
version = "1.0"

dependencies {
    implementation("br.dev.pedrolamarao.gradle.metal.sample:archive:1.0")
}

tasks.wrapper.configure {
    gradleVersion = "8.4-rc-2"
}