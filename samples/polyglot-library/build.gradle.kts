import br.dev.pedrolamarao.gradle.metal.*

plugins {
    id("br.dev.pedrolamarao.metal.library")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.cxx")
}

library {
    compileOptions = listOf("-std=c++20")
}

tasks.compileAsm.configure {
    include("${target.get()}/*")
}