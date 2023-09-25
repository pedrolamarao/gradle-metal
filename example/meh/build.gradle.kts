plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.c")
    id("br.dev.pedrolamarao.metal.base")
}

// register "main" archive with asm and cpp sources

val mainCpp = metal.cpp.sources.create("main")
val mainAsm = metal.asm.sources.create("main")
val mainC = metal.c.sources.create("main")

val mainArchive = metal.archive("main") {
    archiveTask.configure {
        source(
            tasks.named("compile-main-asm"),
            tasks.named("compile-main-c")
        )
    }
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

tasks.assemble.configure {
    dependsOn(archive)
}