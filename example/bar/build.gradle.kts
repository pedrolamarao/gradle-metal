plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.cxx")
}

dependencies {
    nativeImplementation(project(":googletest"))
}

// register "main" archive with cpp and cxx sources

val mainCpp = metal.cpp.sources.create("main")

val mainIxx = metal.ixx.sources.create("main") {
    compileOptions = listOf("-g","--std=c++20")
}

val mainCxx = metal.cxx.sources.create("main") {
    compileOptions = listOf("-g","--std=c++20")
}

val mainArchive = metal.archive("main") {
    archiveTask.configure {
        source(tasks.named("compile-main-cxx"))
    }
}

// register "test" application with cxx sources

val testCxx = metal.cxx.sources.create("test") {
    compileOptions = listOf("-g","--std=c++20")
    modules.from( tasks.named("compile-main-ixx") )
}

val testApplication = metal.application("test") {
    linkTask.configure {
        source(tasks.named("compile-main-cxx"))
        source(tasks.named("compile-test-cxx"))
    }
}

// wire to base tasks

val archive = tasks.register("archive") {
    group = "metal"
    dependsOn(tasks.named("archive-main"))
}

tasks.register("compile") {
    group = "metal"
    dependsOn(
        tasks.named("compile-main-cxx"),
        tasks.named("compile-test-cxx")
    )
}

tasks.register("precompile") {
    group = "metal"
    dependsOn(
        tasks.named("compile-main-ixx"),
    )
}

val link = tasks.register("link") {
    group = "metal"
    dependsOn(
        tasks.named("link-main")
    )
}

tasks.assemble.configure {
    dependsOn(archive,link)
}