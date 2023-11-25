import br.dev.pedrolamarao.gradle.metal.base.MetalCompileTask

plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.asm")
}

tasks.named<MetalCompileTask>("compile-main-asm") {
    include("${metal.target.get()}/*")
}

tasks.wrapper.configure {
    gradleVersion = "8.4"
}