plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.cxx")
    id("br.dev.pedrolamarao.metal.base")
}

dependencies {
    nativeImplementation(project(":bar"))
    nativeImplementation(project(":meh"))
}

// register "main" sources

val mainAsm = metal.asm.sources("main")

val mainCxx = metal.cxx.sources("main") {
    compileOptions = listOf("-g","--std=c++20")
}

// register "main" application

val mainApplication = metal.application("main") {
    linkOptions = listOf("-g")
    linkTask.configure {
        source(mainAsm.objects)
        source(mainCxx.objects)
    }
}

// wire to base tasks

val compile = tasks.register("compile") {
    group = "build"
    dependsOn(mainAsm.compileTask,mainCxx.compileTask);
}

val link = tasks.register("link") {
    group = "build"
    dependsOn(mainApplication.linkTask)
}

tasks.assemble.configure {
    dependsOn(link)
}