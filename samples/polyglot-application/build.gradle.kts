plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.c")
}

tasks.compileAsm.configure {
    include("${target.get()}/*")
}