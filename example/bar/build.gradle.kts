plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.archive")
    id("br.dev.pedrolamarao.metal.cxx")
}

dependencies {
    nativeImplementation(project(":googletest"))
}

// register "main" archive with cpp and cxx sources

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

// register "test" application with cxx sources

metal {
    cpp {
        create("test")
    }
    cxx {
        create("test") {
            compileOptions = listOf("-g","-std=c++20")
            module( tasks.named("compile-main-ixx") )
        }
    }
    applications {
        create("test") {
            source( cxx.named("main").map { it.outputs } )
            source( cxx.named("test").map { it.outputs } )
        }
    }
}

// wire to base tasks

tasks.register("precompile") {
    group = "metal"
    dependsOn(
        tasks.named("compile-main-ixx"),
    )
}