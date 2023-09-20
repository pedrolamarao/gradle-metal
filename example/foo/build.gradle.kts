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

val mainAsm = metal.asm.create("main")

val mainCxx = metal.cxx.create("main")

mainCxx.compileOptions = listOf("-g","--std=c++20")

// register "main" application

val mainApplication = metal.application("main")

mainApplication.linkOptions = listOf("-g")

mainApplication.linkTask.configure {
    source(mainAsm.compileTask.get().outputs.files.asFileTree)
    source(mainCxx.compileTask.get().outputs.files.asFileTree)
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