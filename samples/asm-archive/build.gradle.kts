import br.dev.pedrolamarao.gradle.metal.base.MetalCompileTask

plugins {
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.cpp")
}

tasks.named<MetalCompileTask>("compile-main-asm") {
    include("${metal.target.get()}/*")
}

tasks.named<MetalCompileTask>("compile-test-asm") {
    include("${metal.target.get()}/*")
}

tasks.wrapper.configure {
    gradleVersion = "8.4"
}