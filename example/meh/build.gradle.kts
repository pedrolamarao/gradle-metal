plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.c")
    id("br.dev.pedrolamarao.metal.base")
}

// register "main" archive with asm and cpp sources

val mainCpp = metal.cpp.sources("main")
val mainAsm = metal.asm.sources("main") {
    includeDependencies.from(mainCpp.sources.sourceDirectories)
}
val mainC = metal.c.sources("main") {
    includeDependencies.from(mainCpp.sources.sourceDirectories)
}

val mainArchive = metal.archive("main") {
    archiveTask.configure {
        source(mainAsm.compileTask,mainC.compileTask)
    }
}

// wire to base tasks

tasks.register("compile") {
    group = "metal"
    dependsOn(mainAsm.compileTask,mainC.compileTask);
}

val archive = tasks.register("archive") {
    group = "metal"
    dependsOn(mainArchive.archiveTask)
}

tasks.assemble.configure {
    dependsOn(archive)
}