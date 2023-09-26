plugins {
    id("base")
    id("br.dev.pedrolamarao.metal.asm")
    id("br.dev.pedrolamarao.metal.c")
    id("br.dev.pedrolamarao.metal.base2")
}

// register "main" archive with asm and cpp sources

metal {
    cpp {
        create("main")
    }
    asm {
        create("main") {
            header( cpp.named("main").map { it.sources.sourceDirectories } )
        }
    }
    c {
        create("main") {
            header( cpp.named("main").map { it.sources.sourceDirectories } )
        }
    }
    archives {
        create("main") {
            source(
                asm.named("main").map { it.outputs },
                c.named("main").map { it.outputs }
            )
        }
    }
}

// wire to base tasks

tasks.register("compile") {
    group = "metal"
    dependsOn(
        tasks.named("compile-main-asm"),
        tasks.named("compile-main-c")
    )
}

val archive = tasks.register("archive") {
    group = "metal"
    dependsOn(
        tasks.named("archive-main")
    )
}

tasks.assemble.configure {
    dependsOn(archive)
}