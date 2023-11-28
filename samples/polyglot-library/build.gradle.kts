import br.dev.pedrolamarao.gradle.metal.*

plugins {
    id("br.dev.pedrolamarao.metal.library")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.cxx")
}

library {
    compileOptions = listOf("-std=c++20")
}

tasks.compileAsm.configure {
    include("${target.get()}/*")
}

val compileTestCxx = tasks.register<MetalCxxCompile>("compileTestCxx") {
    importPath = tasks.compileCxx.flatMap { it.importPath }
    options = library.compileOptions
    outputDirectory = file("build/obj/test")
    source = project.files("src/test/cxx").asFileTree
}

val linkTest = tasks.register<MetalLink>("linkTest") {
    linkDependencies.from(tasks.archive)
    output = file("build/exe/test/${target.get()}/test.exe")
    source = objects.fileCollection().from(compileTestCxx).asFileTree
}

val runTest = tasks.register<Exec>("runTest") {
    dependsOn(linkTest)
    executable = linkTest.get().output.get().toString()
}

tasks.check.configure {
    dependsOn(runTest)
}

tasks.wrapper.configure {
    gradleVersion = "8.4"
}