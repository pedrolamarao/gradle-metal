plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.base")
}

// register "main" archive with asm and cpp sources

val mainAsm = metal.asm.sources("main")

val mainCpp = metal.cpp.sources("main")

val mainArchive = metal.archive("main") {
    archiveTask.configure {
        source(mainAsm.compileTask)
    }
}

// wire to base tasks

val compile = tasks.register("compile") {
    group = "metal"
    dependsOn(mainAsm.compileTask);
}

val archive = tasks.register("archive") {
    group = "metal"
    dependsOn(mainArchive.archiveTask)
}

tasks.assemble.configure {
    dependsOn(archive)
}