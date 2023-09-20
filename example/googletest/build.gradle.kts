plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.cxx")
    id("br.dev.pedrolamarao.metal.base")
}

// register "main" archive with cxx sources

val mainCpp = metal.cpp.sources("main")

val mainCxx = metal.cxx.sources("main") {
    compileOptions = listOf("-g","--std=c++17")
    compileTask.configure {
        headerDependencies.from(mainCpp.sources.sourceDirectories)
    }
    sources.exclude("**/*.h")
}

val mainArchive = metal.archive("main") {
    archiveTask.configure {
        source(mainCxx.compileTask)
    }
}

// wire to base tasks

val archive = tasks.register("archive") {
    group = "metal"
    dependsOn(mainArchive.archiveTask)
}

val compile = tasks.register("compile") {
    group = "metal"
    dependsOn(mainCxx.compileTask);
}

tasks.assemble.configure {
    dependsOn(archive)
}