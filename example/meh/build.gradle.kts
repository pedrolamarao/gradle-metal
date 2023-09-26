plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.c")
}

// wire to base tasks

tasks.register("compile") {
    group = "metal"
    dependsOn(
        tasks.named("compile-main-asm"),
        tasks.named("compile-main-c")
    )
}

val archive = tasks.register("archive") {
    group = "metal"
    dependsOn(
        tasks.named("archive-main")
    )
}