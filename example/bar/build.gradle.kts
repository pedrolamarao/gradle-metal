plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.cxx")
    id("br.dev.pedrolamarao.metal.base")
}

// register "main" sources

val mainCxx = metal.cxx.create("main") {
    compileOptions = listOf("-g","--std=c++20")
}

// register "main" archive

val mainArchive = metal.archive("main") {
    archiveTask.configure {
        source(mainCxx.compileTask.get().outputs.files.asFileTree)
    }
}

// wire to base tasks

val compile = tasks.register("compile") {
    group = "build"
    dependsOn(mainCxx.compileTask);
}

val archive = tasks.register("archive") {
    group = "build"
    dependsOn(mainArchive.archiveTask)
}

tasks.assemble.configure {
    dependsOn(archive)
}