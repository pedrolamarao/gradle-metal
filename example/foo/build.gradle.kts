import br.dev.pedrolamarao.gradle.metal.base.NativeLinkTask

plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.cpp")
    id("br.dev.pedrolamarao.metal.cxx")
    id("br.dev.pedrolamarao.metal.application")
}

dependencies {
    nativeImplementation(project(":bar"))
    nativeImplementation(project(":meh"))
}

// register "main" sources

val mainAsm = asm.create("main")

val mainCxx = cxx.create("main")

mainCxx.options.languageDialect = "c++20"

// register "main" executable

val mainLinkOptions = emptyList<String>()

val linkMain = tasks.register<NativeLinkTask>("linkMain") {
    libraryDependencies.from(configurations.nativeLinkDependencies)
    output = project.layout.buildDirectory.file("exe/main/${project.name}.exe")
    options = mainLinkOptions
    source = mainCxx.compileTask.get().outputs.files.asFileTree + mainAsm.compileTask.get().outputs.files.asFileTree
}

// wire to base tasks

val compile = tasks.register("compile") {
    group = "build"
    dependsOn(mainAsm.compileTask,mainCxx.compileTask);
}

val link = tasks.register("link") {
    group = "build"
    dependsOn(linkMain)
}

tasks.assemble.configure {
    dependsOn(link)
}