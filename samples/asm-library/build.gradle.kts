plugins {
    id("br.dev.pedrolamarao.metal.library")
    id("br.dev.pedrolamarao.metal.asm")
}

library {
    include("${target.get()}/*")
}

test {
    include("${target.get()}/*")
}