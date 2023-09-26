plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.cxx")
    id("br.dev.pedrolamarao.metal.base2")
}

dependencies {
    nativeImplementation(project(":bar"))
    nativeImplementation(project(":meh"))
}

// register "main" application with cxx sources

metal {
    ixx {
        sources {
            create("main") {
                compileOptions = listOf("-g","--std=c++20")
            }
        }
    }
    cxx {
        sources {
            create("main") {
                compileOptions = listOf("-g","--std=c++20")
                module( ixx.sources.named("main").map { it.outputDirectory } )
            }
        }
    }
    applications {
        create("main") {
            linkOptions = listOf("-g")
            source( cxx.sources.named("main").map { it.outputs } )
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

tasks.assemble.configure {
    dependsOn(link)
}