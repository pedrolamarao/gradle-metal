import br.dev.pedrolamarao.gradle.metal.base.MetalArchiveTask
import br.dev.pedrolamarao.gradle.metal.base.MetalLinkTask
import br.dev.pedrolamarao.gradle.metal.c.MetalCCompileTask;
import br.dev.pedrolamarao.gradle.metal.cxx.MetalCxxCompileTask

plugins {
    id("br.dev.pedrolamarao.metal.base")
}

// register C source compile task for lib
val compileLib = tasks.register<MetalCCompileTask>("compile-lib") {
    outputDirectory = layout.projectDirectory.dir("obj/lib")
    source( layout.projectDirectory.files("lib") )
}

// register C++ source compile task for tool
val compileTool = tasks.register<MetalCxxCompileTask>("compile-tool") {
    includables.from( layout.projectDirectory.dir("lib") )
    outputDirectory = layout.projectDirectory.dir("obj/tool")
    source( layout.projectDirectory.files("tool") )
}

// register archive task for lib
val archiveLib = tasks.register<MetalArchiveTask>("archive-lib") {
    outputDirectory = layout.projectDirectory.dir("bin")
    source( compileLib )
}

// register link task for lib
val linkTool = tasks.register<MetalLinkTask>("link-tool") {
    outputDirectory = layout.projectDirectory.dir("bin")
    source( compileTool, archiveLib )
}

// wire lifecycle task
tasks.assemble.configure {
    dependsOn("link-tool")
}

// wire lifecycle task
tasks.clean.configure {
    delete("bin")
    delete("obj")
}