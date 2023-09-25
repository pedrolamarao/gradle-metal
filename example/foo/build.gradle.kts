plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.cxx")
    id("br.dev.pedrolamarao.metal.base")
}

dependencies {
    nativeImplementation(project(":bar"))
    nativeImplementation(project(":meh"))
}

// register "main" application with cxx sources

val mainIxx = metal.ixx.sources.create("main") {
    compileOptions = listOf("-g","--std=c++20")
}

val mainCxx = metal.cxx.sources.create("main") {
    compileOptions = listOf("-g","--std=c++20")
}

val mainApplication = metal.application("main") {
    linkOptions = listOf("-g")
    linkTask.configure {
        source(
            tasks.named("compile-main-cxx")
        )
    }
}

// wire to base tasks

tasks.register("compile") {
    group = "metal"
    dependsOn(
        tasks.named("compile-main-cxx"))
}

tasks.register("precompile") {
    group = "metal"
    dependsOn(
        tasks.named("compile-main-ixx"))
}

val link = tasks.register("link") {
    group = "metal"
    dependsOn(
        tasks.named("link-main")
    )
}

tasks.assemble.configure {
    dependsOn(link)
}