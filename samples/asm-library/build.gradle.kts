plugins {
    id("br.dev.pedrolamarao.metal.library")
    id("br.dev.pedrolamarao.metal.asm")
}

tasks.compileAsm.configure {
    include("${target.get()}/*")
}

tasks.compileTestAsm.configure {
    include("${target.get()}/*")
}