plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.cxx")
    id("br.dev.pedrolamarao.metal.base")
}

// register "main" archive with cxx sources

val mainCpp = metal.cpp.sources.create("main")

val mainCxx = metal.cxx.sources.create("main") {
    compileOptions = listOf("-g","--std=c++17")
    sources.exclude("**/*.h")
}

val mainArchive = metal.archive("main") {
    archiveTask.configure {
        source(
            tasks.named("compile-main-cxx")
        )
    }
}

// wire to base tasks

val archive = tasks.register("archive") {
    group = "metal"
    dependsOn(
        tasks.named("archive-main")
    )
}

val compile = tasks.register("compile") {
    group = "metal"
    dependsOn(
        tasks.named("compile-main-cxx")
    )
}

tasks.assemble.configure {
    dependsOn(archive)
}