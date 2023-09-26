plugins {
    id("br.dev.pedrolamarao.metal.application")
    id("br.dev.pedrolamarao.metal.cxx")
}

dependencies {
    nativeImplementation(project(":bar"))
    nativeImplementation(project(":meh"))
}

metal {
    ixx {
        named("main") {
            compileOptions = listOf("-g","-std=c++20")
        }
    }
    cxx {
        named("main") {
            compileOptions = listOf("-g","-std=c++20")
        }
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