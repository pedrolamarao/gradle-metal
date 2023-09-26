plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.base2")
    id("br.dev.pedrolamarao.metal.cxx")
}

dependencies {
    nativeImplementation(project(":googletest"))
}

// register "main" archive with cpp and cxx sources

metal {
    cpp {
        sources {
            create("main")
        }
    }
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
    archives {
        create("main") {
            source( cxx.sources.named("main").map { it.outputs } )
        }
    }
}

// register "test" application with cxx sources

metal {
    cpp {
        sources {
            create("test")
        }
    }
    cxx {
        sources {
            create("test") {
                compileOptions = listOf("-g","-std=c++20")
                module( tasks.named("compile-main-ixx") )
            }
        }
    }
    applications {
        create("test") {
            source( cxx.sources.named("main").map { it.outputs } )
            source( cxx.sources.named("test").map { it.outputs } )
        }
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
        tasks.named("link-test")
    )
}

tasks.assemble.configure {
    dependsOn(archive,link)
}