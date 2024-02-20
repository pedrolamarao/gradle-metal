plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.asm")
}

tasks.compileAsm.configure {
    include("${target.get()}/*")
}

tasks.compileAsmCommands.configure {
    include("${target.get()}/*")
}