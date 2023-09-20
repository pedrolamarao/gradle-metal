import br.dev.pedrolamarao.gradle.metal.base.NativeArchiveTask

plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.archive")
}

// register "main" sources

// add assembler sources

val mainAsm = metal.asm.create("main")

val mainCpp = metal.cpp.create("main")

// add "main" archive

val mainArchiveOptions = emptyList<String>()

val archiveMain = tasks.register<NativeArchiveTask>("archiveMain") {
    output = project.layout.buildDirectory.file("lib/main/${project.name}.lib")
    source = mainAsm.compileTask.get().outputs.files.asFileTree
}

configurations.nativeLinkElements.configure {
    outgoing.artifact(archiveMain)
}

// wire to base tasks

val compile = tasks.register("compile") {
    group = "native"
    dependsOn(mainAsm.compileTask);
}

val archive = tasks.register("archive") {
    group = "native"
    dependsOn(archiveMain)
}

tasks.assemble.configure {
    dependsOn(archive)
}