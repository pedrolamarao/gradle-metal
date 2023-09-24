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

val mainIxx = metal.ixx.sources("main") {
    compileOptions = listOf("-g","--std=c++20")
}

val mainCxx = metal.cxx.sources("main") {
    compileOptions = listOf("-g","--std=c++20")
    importDependencies.from(mainIxx.compileTask)
    compileTask.configure {
        source(mainIxx.compileTask)
    }
}

val mainApplication = metal.application("main") {
    linkOptions = listOf("-g")
    linkTask.configure {
        source(mainCxx.compileTask)
    }
}

// wire to base tasks

tasks.register("compile") {
    group = "metal"
    dependsOn(mainCxx.compileTask)
}

val link = tasks.register("link") {
    group = "metal"
    dependsOn(mainApplication.linkTask)
}

tasks.assemble.configure {
    dependsOn(link)
}