import br.dev.pedrolamarao.gradle.metal.base.NativeArchiveTask

plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.base")
}

// register "main" sources

val mainAsm = metal.asm.create("main")

val mainCpp = metal.cpp.create("main")

// add "main" archive

val mainArchive = metal.archive("main") {
    archiveTask.configure {
        source(mainAsm.compileTask.get().outputs.files.asFileTree)
    }
}

// wire to base tasks

val compile = tasks.register("compile") {
    group = "native"
    dependsOn(mainAsm.compileTask);
}

val archive = tasks.register("archive") {
    group = "native"
    dependsOn(mainArchive.archiveTask)
}

tasks.assemble.configure {
    dependsOn(archive)
}