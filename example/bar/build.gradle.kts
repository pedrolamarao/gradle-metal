import br.dev.pedrolamarao.gradle.metal.base.NativeArchiveTask

plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.cxx")
    id("br.dev.pedrolamarao.metal.archive")
}

// register "main" sources

val mainCxx = metal.cxx.create("main")

mainCxx.options.languageDialect = "c++20"

// register "main" archive

val mainArchiveOptions = emptyList<String>()

val archiveMain = tasks.register<NativeArchiveTask>("archiveMain") {
    output = project.layout.buildDirectory.file("lib/main/${project.name}.lib")
    source = mainCxx.compileTask.get().outputs.files.asFileTree
}

configurations.nativeLinkElements.configure {
    outgoing.artifact(archiveMain)
}

// wire to base tasks

val compile = tasks.register("compile") {
    group = "build"
    dependsOn(mainCxx.compileTask);
}

val archive = tasks.register("archive") {
    group = "build"
    dependsOn(archiveMain)
}

tasks.assemble.configure {
    dependsOn(archive)
}